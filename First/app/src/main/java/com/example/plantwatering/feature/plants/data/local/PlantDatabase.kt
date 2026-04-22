package com.example.plantwatering.feature.plants.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlantEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        fun create(context: Context): PlantDatabase {
            return Room.databaseBuilder(
                context,
                PlantDatabase::class.java,
                "plants.db"
            )
                .fallbackToDestructiveMigration(false)
                .build()
        }
    }
}
