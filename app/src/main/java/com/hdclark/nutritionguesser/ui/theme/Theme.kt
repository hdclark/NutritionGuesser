package com.hdclark.nutritionguesser.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4B7F3B), onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEDBF), onPrimaryContainer = Color(0xFF10290B),
    secondary = Color(0xFFE76F51), onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD0), onSecondaryContainer = Color(0xFF3B0A00),
    tertiary = Color(0xFFFFB703), onTertiary = Color(0xFF2B1D00),
    background = Color(0xFFFFF8E7), onBackground = Color(0xFF232117),
    surface = Color(0xFFFFFBF2), onSurface = Color(0xFF232117),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB1D7A2), onPrimary = Color(0xFF1D3617),
    primaryContainer = Color(0xFF34502A), onPrimaryContainer = Color(0xFFCDEDBF),
    secondary = Color(0xFFFFB4A1), onSecondary = Color(0xFF5E1607),
    tertiary = Color(0xFFFFC94D), onTertiary = Color(0xFF422C00),
    background = Color(0xFF17150E), onBackground = Color(0xFFEAE2D2),
    surface = Color(0xFF211F17), onSurface = Color(0xFFEAE2D2),
)

@Composable
fun NutritionGuesserTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
