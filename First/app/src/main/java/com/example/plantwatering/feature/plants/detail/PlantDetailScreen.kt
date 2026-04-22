package com.example.plantwatering.feature.plants.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plantwatering.R
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    viewModel: PlantDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.plant_detail_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showDeleteConfirmation && state.plant != null) {
            AlertDialog(
                onDismissRequest = {
                    if (!state.isDeleting) {
                        showDeleteConfirmation = false
                    }
                },
                title = { Text(stringResource(R.string.plant_delete_confirm_title)) },
                text = { Text(stringResource(R.string.plant_delete_confirm_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                            viewModel.deletePlant()
                        },
                        enabled = !state.isDeleting
                    ) {
                        Text(stringResource(if (state.isDeleting) R.string.deleting else R.string.delete_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false },
                        enabled = !state.isDeleting
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.plant == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(state.errorMessageRes ?: R.string.plant_not_available))
                }
            }

            else -> {
                val plant = requireNotNull(state.plant)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    InfoLine(
                        label = stringResource(R.string.plant_detail_interval_label),
                        value = stringResource(R.string.plant_detail_interval_value, plant.wateringIntervalDays)
                    )
                    InfoLine(
                        label = stringResource(R.string.plant_detail_last_watered_label),
                        value = formatDateTime(plant.lastWateredAtMillis)
                    )
                    InfoLine(
                        label = stringResource(R.string.plant_detail_next_reminder_label),
                        value = formatDateTime(plant.nextWateringAtMillis)
                    )
                    InfoLine(
                        label = stringResource(R.string.plant_detail_status_label),
                        value = stringResource(
                            if (plant.isDue) R.string.plant_detail_status_due else R.string.plant_detail_status_not_due
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::markPlantWatered
                    ) {
                        Text(stringResource(R.string.plant_detail_mark_watered))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showDeleteConfirmation = true },
                        enabled = !state.isDeleting
                    ) {
                        Text(stringResource(if (state.isDeleting) R.string.deleting else R.string.delete_plant_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatDateTime(timeInMillis: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(timeInMillis))
}
