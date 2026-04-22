package com.example.plantwatering.feature.plants.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantwatering.R
import com.example.plantwatering.core.notifications.PlantReminderScheduler
import com.example.plantwatering.feature.plants.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantId: String,
    private val repository: PlantRepository,
    private val reminderScheduler: PlantReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState.asStateFlow()

    init {
        observePlant()
    }

    fun markPlantWatered() {
        viewModelScope.launch {
            repository.markPlantWatered(plantId, System.currentTimeMillis())
        }
    }

    fun deletePlant() {
        if (_uiState.value.isDeleting || _uiState.value.isDeleted) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessageRes = null) }
            runCatching {
                reminderScheduler.cancel(plantId)
                repository.deletePlant(plantId)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        plant = null,
                        isLoading = false,
                        errorMessageRes = null,
                        isDeleting = false,
                        isDeleted = true
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.plant_delete_error,
                        isDeleting = false
                    )
                }
            }
        }
    }

    private fun observePlant() {
        viewModelScope.launch {
            repository.observePlant(plantId)
                .catch { throwable ->
                    _uiState.value = PlantDetailUiState(
                        isLoading = false,
                        errorMessageRes = R.string.plant_load_error
                    )
                }
                .collect { plant ->
                    if (_uiState.value.isDeleted) return@collect
                    plant?.let(reminderScheduler::schedule)
                    _uiState.update {
                        it.copy(
                            plant = plant,
                            isLoading = false,
                            errorMessageRes = if (plant == null) R.string.plant_not_found else null,
                            isDeleting = false
                        )
                    }
                }
        }
    }
}
