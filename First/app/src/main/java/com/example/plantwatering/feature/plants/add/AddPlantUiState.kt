package com.example.plantwatering.feature.plants.add

import androidx.annotation.StringRes

data class AddPlantUiState(
    val plantName: String = "",
    val wateringIntervalInput: String = "",
    val isSaving: Boolean = false,
    @field:StringRes val errorMessageRes: Int? = null
)
