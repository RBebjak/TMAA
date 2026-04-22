package com.example.plantwatering.feature.plants.data.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.plantwatering.core.notifications.PlantReminderScheduler
import com.example.plantwatering.feature.plants.data.local.PlantDatabase
import com.example.plantwatering.feature.plants.data.remote.FirestorePlantSyncDataSource
import com.example.plantwatering.feature.plants.data.repository.RoomPlantRepository
import com.example.plantwatering.feature.plants.detail.PlantDetailViewModel
import com.example.plantwatering.feature.plants.add.AddPlantViewModel
import com.example.plantwatering.feature.plants.domain.repository.PlantRepository
import com.example.plantwatering.feature.plants.list.PlantListViewModel

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database = PlantDatabase.create(appContext)
    private val reminderScheduler = PlantReminderScheduler(appContext)
    private val firestorePlantSyncDataSource = FirestorePlantSyncDataSource(appContext)
    private val repository: PlantRepository = RoomPlantRepository(
        plantDao = database.plantDao(),
        firestorePlantSyncDataSource = firestorePlantSyncDataSource
    )

    fun plantListViewModelFactory(): ViewModelProvider.Factory = factory {
        PlantListViewModel(repository, reminderScheduler)
    }

    fun plantDetailViewModelFactory(plantId: String): ViewModelProvider.Factory = factory {
        PlantDetailViewModel(plantId, repository, reminderScheduler)
    }

    fun addPlantViewModelFactory(): ViewModelProvider.Factory = factory {
        AddPlantViewModel(repository)
    }
}

private inline fun <reified T : ViewModel> factory(
    crossinline create: () -> T
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM {
            return create() as VM
        }
    }
}
