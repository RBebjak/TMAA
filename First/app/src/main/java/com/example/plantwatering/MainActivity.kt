package com.example.plantwatering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.plantwatering.core.navigation.PlantWateringApp
import com.example.plantwatering.core.ui.theme.PlantWateringTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantWateringTheme {
                PlantWateringApp(
                    appContainer = (application as PlantWateringApplication).container
                )
            }
        }
    }
}
