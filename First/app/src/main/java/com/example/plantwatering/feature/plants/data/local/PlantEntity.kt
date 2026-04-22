package com.example.plantwatering.feature.plants.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.plantwatering.feature.plants.domain.model.Plant
import java.util.UUID

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val wateringIntervalDays: Long,
    val lastWateredAtMillis: Long
) {
    fun toDomain(): Plant {
        return Plant(
            id = id,
            name = name,
            wateringIntervalDays = wateringIntervalDays,
            lastWateredAtMillis = lastWateredAtMillis
        )
    }

    companion object {
        fun create(name: String, wateringIntervalDays: Long, nowMillis: Long): PlantEntity {
            return PlantEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                wateringIntervalDays = wateringIntervalDays,
                lastWateredAtMillis = nowMillis
            )
        }
    }
}
