package com.example.plantwatering.feature.plants.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY lastWateredAtMillis ASC")
    fun observePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants ORDER BY lastWateredAtMillis ASC")
    suspend fun getPlants(): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    fun observePlant(plantId: String): Flow<PlantEntity?>

    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    suspend fun getPlantById(plantId: String): PlantEntity?

    @Query(
        """
        INSERT OR REPLACE INTO plants (
            id,
            name,
            wateringIntervalDays,
            lastWateredAtMillis
        ) VALUES (
            :id,
            :name,
            :wateringIntervalDays,
            :lastWateredAtMillis
        )
        """
    )
    suspend fun insertPlant(
        id: String,
        name: String,
        wateringIntervalDays: Long,
        lastWateredAtMillis: Long
    )

    @Query("UPDATE plants SET lastWateredAtMillis = :wateredAtMillis WHERE id = :plantId")
    suspend fun markPlantWatered(plantId: String, wateredAtMillis: Long)

    @Query("DELETE FROM plants WHERE id = :plantId")
    suspend fun deletePlant(plantId: String)
}
