package com.example.plantwatering.feature.plants.detail

import androidx.annotation.StringRes
import com.example.plantwatering.feature.plants.domain.model.Plant

data class PlantDetailUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
    @field:StringRes val errorMessageRes: Int? = null,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)
