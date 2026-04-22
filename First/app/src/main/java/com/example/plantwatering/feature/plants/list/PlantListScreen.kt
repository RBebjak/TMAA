package com.example.plantwatering.feature.plants.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plantwatering.R
import com.example.plantwatering.feature.plants.domain.model.Plant
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    viewModel: PlantListViewModel,
    onAddPlantClick: () -> Unit,
    onPlantClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.plant_list_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlantClick) {
                Text(stringResource(R.string.plant_list_add_button))
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SyncActions(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    isUploading = state.isUploading,
                    isDownloading = state.isDownloading,
                    onUpload = viewModel::uploadPlants,
                    onDownload = viewModel::downloadPlants
                )
                state.syncMessageRes?.let { syncMessageRes ->
                    Text(
                        text = stringResource(syncMessageRes),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                state.errorMessageRes?.let { errorMessageRes ->
                    Text(
                        text = stringResource(errorMessageRes),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.plants.isEmpty() -> {
                        EmptyPlantList(modifier = Modifier.fillMaxSize())
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.plants,
                                key = { it.id }
                            ) { plant ->
                                PlantListItem(
                                    plant = plant,
                                    onClick = { onPlantClick(plant.id) },
                                    onWaterNow = { viewModel.markPlantWatered(plant.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncActions(
    modifier: Modifier = Modifier,
    isUploading: Boolean,
    isDownloading: Boolean,
    onUpload: () -> Unit,
    onDownload: () -> Unit
) {
    Row(modifier = modifier) {
        Button(
            modifier = Modifier.weight(1f),
            enabled = !isUploading && !isDownloading,
            onClick = onUpload
        ) {
            Text(
                stringResource(
                    if (isUploading) R.string.plant_list_upload_in_progress else R.string.plant_list_upload
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            modifier = Modifier.weight(1f),
            enabled = !isUploading && !isDownloading,
            onClick = onDownload
        ) {
            Text(
                stringResource(
                    if (isDownloading) R.string.plant_list_download_in_progress else R.string.plant_list_download
                )
            )
        }
    }
}

@Composable
private fun EmptyPlantList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.plant_list_empty_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.plant_list_empty_message),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PlantListItem(
    plant: Plant,
    onClick: () -> Unit,
    onWaterNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.plant_list_watering_every, plant.wateringIntervalDays),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                DueBadge(isDue = plant.isDue)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.plant_list_next_watering, formatDateTime(plant.nextWateringAtMillis)),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onWaterNow) {
                Text(stringResource(R.string.plant_list_mark_watered))
            }
        }
    }
}

@Composable
private fun DueBadge(isDue: Boolean) {
    val label = stringResource(if (isDue) R.string.plant_due else R.string.plant_healthy)
    val containerColor = if (isDue) {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }
    val textColor = if (isDue) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun formatDateTime(timeInMillis: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(timeInMillis))
}
