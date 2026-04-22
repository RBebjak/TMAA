package com.example.plantwatering.feature.plants.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantwatering.R
import com.example.plantwatering.feature.plants.domain.repository.PlantRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPlantViewModel(
    private val repository: PlantRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPlantUiState())
    val uiState: StateFlow<AddPlantUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<AddPlantEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onPlantNameChanged(value: String) {
        _uiState.update { it.copy(plantName = value, errorMessageRes = null) }
    }

    fun onWateringIntervalChanged(value: String) {
        _uiState.update { it.copy(wateringIntervalInput = value, errorMessageRes = null) }
    }

    fun savePlant() {
        val currentState = _uiState.value
        val days = currentState.wateringIntervalInput.toLongOrNull()

        if (currentState.plantName.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.add_plant_name_required) }
            return
        }
        if (days == null || days <= 0) {
            _uiState.update { it.copy(errorMessageRes = R.string.add_plant_interval_required) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessageRes = null) }
            runCatching {
                repository.addPlant(
                    name = currentState.plantName.trim(),
                    wateringIntervalDays = days
                )
            }.onSuccess {
                eventsChannel.send(AddPlantEvent.Saved)
                _uiState.value = AddPlantUiState()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessageRes = R.string.add_plant_save_error
                    )
                }
            }
        }
    }
}

sealed interface AddPlantEvent {
    data object Saved : AddPlantEvent
}
