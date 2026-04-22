package com.example.plantwatering.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LeafGreen,
    secondary = MossGreen,
    tertiary = SoilBrown,
    background = SoftCream,
    surface = White,
    surfaceVariant = MistBlue,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Slate,
    onSurface = Slate,
    onSurfaceVariant = Slate
)

private val DarkColors = darkColorScheme(
    primary = MossGreen,
    secondary = LeafGreen,
    tertiary = SoilBrown,
    background = Slate,
    surface = ColorTokens.DarkSurface,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = SoftCream,
    onSurface = SoftCream
)

@Composable
fun PlantWateringTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = PlantWateringTypography,
        content = content
    )
}

private object ColorTokens {
    val DarkSurface = Color(0xFF294239)
}
