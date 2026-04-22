package com.example.plantwatering.feature.plants.list

import androidx.annotation.StringRes
import com.example.plantwatering.feature.plants.domain.model.Plant

data class PlantListUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
    val isUploading: Boolean = false,
    val isDownloading: Boolean = false,
    @field:StringRes val errorMessageRes: Int? = null,
    @field:StringRes val syncMessageRes: Int? = null
)
