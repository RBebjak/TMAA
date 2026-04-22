package com.example.plantwatering.core.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plantwatering.feature.plants.domain.model.Plant
import com.example.plantwatering.feature.plants.worker.PlantReminderWorker
import java.util.concurrent.TimeUnit
import kotlin.math.max

class PlantReminderScheduler(
    private val context: Context
) {
    fun schedule(plant: Plant) {
        if (plant.isDue) {
            cancel(plant.id)
            return
        }

        val delay = max(plant.nextWateringAtMillis - System.currentTimeMillis(), 0L)
        val inputData = Data.Builder()
            .putString(PlantReminderWorker.keyPlantId, plant.id)
            .build()

        val request = OneTimeWorkRequestBuilder<PlantReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(plantWorkName(plant.id))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            plantWorkName(plant.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(plantId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(plantWorkName(plantId))
    }

    private fun plantWorkName(plantId: String): String = "plant-reminder-$plantId"
}
