package com.example.plantwatering.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plantwatering.core.notifications.NotificationPermissionEffect
import com.example.plantwatering.feature.plants.add.AddPlantScreen
import com.example.plantwatering.feature.plants.add.AddPlantViewModel
import com.example.plantwatering.feature.plants.data.di.AppContainer
import com.example.plantwatering.feature.plants.detail.PlantDetailScreen
import com.example.plantwatering.feature.plants.detail.PlantDetailViewModel
import com.example.plantwatering.feature.plants.list.PlantListScreen
import com.example.plantwatering.feature.plants.list.PlantListViewModel

@Composable
fun PlantWateringApp(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NotificationPermissionEffect()

    NavHost(
        navController = navController,
        startDestination = Routes.PlantList.route,
        modifier = modifier
    ) {
        composable(Routes.PlantList.route) {
            val viewModel: PlantListViewModel = viewModel(
                factory = appContainer.plantListViewModelFactory()
            )
            PlantListScreen(
                viewModel = viewModel,
                onAddPlantClick = { navController.navigate(Routes.AddPlant.route) },
                onPlantClick = { plantId ->
                    navController.navigate(Routes.PlantDetail.createRoute(plantId))
                }
            )
        }

        composable(Routes.AddPlant.route) {
            val viewModel: AddPlantViewModel = viewModel(
                factory = appContainer.addPlantViewModelFactory()
            )
            AddPlantScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPlantSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PlantDetail.route,
            arguments = listOf(navArgument(Routes.PlantDetail.argumentName) { type = NavType.StringType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString(Routes.PlantDetail.argumentName).orEmpty()
            val viewModel: PlantDetailViewModel = viewModel(
                factory = appContainer.plantDetailViewModelFactory(plantId)
            )
            PlantDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Routes(val route: String) {
    data object PlantList : Routes("plant_list")
    data object AddPlant : Routes("add_plant")
    data object PlantDetail : Routes("plant_detail/{plantId}") {
        const val argumentName = "plantId"

        fun createRoute(plantId: String): String = "plant_detail/$plantId"
    }
}
