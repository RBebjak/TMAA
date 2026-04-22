package com.example.plantwatering.feature.plants.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantwatering.core.notifications.PlantReminderNotifier
import com.example.plantwatering.core.notifications.PlantReminderScheduler
import com.example.plantwatering.feature.plants.data.local.PlantDatabase

class PlantReminderWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val plantId = inputData.getString(keyPlantId) ?: return Result.failure()
        val plant = PlantDatabase.create(applicationContext)
            .plantDao()
            .getPlantById(plantId)
            ?.toDomain()
            ?: return Result.success()

        if (plant.isDue) {
            PlantReminderNotifier.showReminder(
                context = applicationContext,
                plantId = plant.id,
                plantName = plant.name
            )
        }

        PlantReminderScheduler(applicationContext).schedule(plant)
        return Result.success()
    }

    companion object {
        const val keyPlantId = "plant_id"
    }
}
