package com.example.plantwatering

import android.app.Application
import com.example.plantwatering.core.notifications.PlantReminderNotifier
import com.example.plantwatering.feature.plants.data.di.AppContainer

class PlantWateringApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        PlantReminderNotifier.createChannel(this)
        container = AppContainer(this)
    }
}
