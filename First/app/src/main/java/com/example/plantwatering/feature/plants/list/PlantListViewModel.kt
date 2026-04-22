package com.example.plantwatering.feature.plants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantwatering.R
import com.example.plantwatering.core.firebase.FirestoreSyncException
import com.example.plantwatering.core.firebase.FirebaseConfigurationException
import com.example.plantwatering.core.notifications.PlantReminderScheduler
import com.example.plantwatering.feature.plants.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlantListViewModel(
    private val repository: PlantRepository,
    private val reminderScheduler: PlantReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlantListUiState())
    val uiState: StateFlow<PlantListUiState> = _uiState.asStateFlow()

    init {
        observePlants()
    }

    fun markPlantWatered(plantId: String) {
        viewModelScope.launch {
            repository.markPlantWatered(plantId, System.currentTimeMillis())
        }
    }

    fun uploadPlants() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploading = true,
                    syncMessageRes = null
                )
            }
            runCatching {
                repository.uploadPlantsToCloud()
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        syncMessageRes = R.string.plant_list_upload_success
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        syncMessageRes = when (throwable) {
                            is FirebaseConfigurationException -> R.string.firebase_not_configured
                            is FirestoreSyncException.Unreachable -> R.string.firestore_unreachable
                            is FirestoreSyncException.PermissionDenied -> R.string.firestore_permission_denied
                            else -> R.string.plant_list_upload_error
                        }
                    )
                }
            }
        }
    }

    fun downloadPlants() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true,
                    syncMessageRes = null
                )
            }
            runCatching {
                repository.downloadPlantsFromCloud()
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        syncMessageRes = R.string.plant_list_download_success
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        syncMessageRes = when (throwable) {
                            is FirebaseConfigurationException -> R.string.firebase_not_configured
                            is FirestoreSyncException.Unreachable -> R.string.firestore_unreachable
                            is FirestoreSyncException.PermissionDenied -> R.string.firestore_permission_denied
                            else -> R.string.plant_list_download_error
                        }
                    )
                }
            }
        }
    }

    private fun observePlants() {
        viewModelScope.launch {
            repository.observePlants()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.plant_list_load_error
                        )
                    }
                }
                .collect { plants ->
                    plants.forEach(reminderScheduler::schedule)
                    _uiState.value = PlantListUiState(
                        plants = plants.sortedBy { it.nextWateringAtMillis },
                        isLoading = false,
                        isUploading = _uiState.value.isUploading,
                        isDownloading = _uiState.value.isDownloading,
                        errorMessageRes = null,
                        syncMessageRes = _uiState.value.syncMessageRes
                    )
                }
        }
    }
}
