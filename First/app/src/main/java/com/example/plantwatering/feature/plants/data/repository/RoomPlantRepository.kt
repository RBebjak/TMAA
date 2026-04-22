package com.example.plantwatering.feature.plants.data.repository

import com.example.plantwatering.feature.plants.data.local.PlantDao
import com.example.plantwatering.feature.plants.data.local.PlantEntity
import com.example.plantwatering.feature.plants.data.remote.FirestorePlantSyncDataSource
import com.example.plantwatering.feature.plants.domain.model.Plant
import com.example.plantwatering.feature.plants.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPlantRepository(
    private val plantDao: PlantDao,
    private val firestorePlantSyncDataSource: FirestorePlantSyncDataSource
) : PlantRepository {
    override fun observePlants(): Flow<List<Plant>> {
        return plantDao.observePlants().map { plants -> plants.map(PlantEntity::toDomain) }
    }

    override fun observePlant(plantId: String): Flow<Plant?> {
        return plantDao.observePlant(plantId).map { plant -> plant?.toDomain() }
    }

    override suspend fun getPlant(plantId: String): Plant? {
        return plantDao.getPlantById(plantId)?.toDomain()
    }

    override suspend fun addPlant(name: String, wateringIntervalDays: Long) {
        val plant = PlantEntity.create(
            name = name,
            wateringIntervalDays = wateringIntervalDays,
            nowMillis = System.currentTimeMillis()
        )
        plantDao.insertPlant(
            id = plant.id,
            name = plant.name,
            wateringIntervalDays = plant.wateringIntervalDays,
            lastWateredAtMillis = plant.lastWateredAtMillis
        )
    }

    override suspend fun markPlantWatered(plantId: String, wateredAtMillis: Long) {
        plantDao.markPlantWatered(plantId, wateredAtMillis)
    }

    override suspend fun deletePlant(plantId: String) {
        plantDao.deletePlant(plantId)
    }

    override suspend fun uploadPlantsToCloud() {
        firestorePlantSyncDataSource.uploadPlants(
            plantDao.getPlants().map(PlantEntity::toDomain)
        )
    }

    override suspend fun downloadPlantsFromCloud() {
        firestorePlantSyncDataSource.downloadPlants().forEach { plant ->
            plantDao.insertPlant(
                id = plant.id,
                name = plant.name,
                wateringIntervalDays = plant.wateringIntervalDays,
                lastWateredAtMillis = plant.lastWateredAtMillis
            )
        }
    }
}
