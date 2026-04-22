package com.example.plantwatering.feature.plants.domain.repository

import com.example.plantwatering.feature.plants.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun observePlants(): Flow<List<Plant>>
    fun observePlant(plantId: String): Flow<Plant?>
    suspend fun getPlant(plantId: String): Plant?
    suspend fun addPlant(name: String, wateringIntervalDays: Long)
    suspend fun markPlantWatered(plantId: String, wateredAtMillis: Long)
    suspend fun deletePlant(plantId: String)
    suspend fun uploadPlantsToCloud()
    suspend fun downloadPlantsFromCloud()
}
