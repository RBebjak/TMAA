package com.example.plantwatering.feature.plants.domain.model

data class Plant(
    val id: String,
    val name: String,
    val wateringIntervalDays: Long,
    val lastWateredAtMillis: Long
) {
    val nextWateringAtMillis: Long
        get() = lastWateredAtMillis + wateringIntervalDays * 24 * 60 * 60 * 1000

    val isDue: Boolean
        get() = nextWateringAtMillis <= System.currentTimeMillis()
}
