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
    surfaceVariant = Color(0xFFE5E7DE), onSurfaceVariant = Color(0xFF45483F),
    error = Color(0xFFBA1A1A), onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC7E8B9), onPrimary = Color(0xFF173811),
    primaryContainer = Color(0xFF34502A), onPrimaryContainer = Color(0xFFE2FFD5),
    secondary = Color(0xFFFFC2B2), onSecondary = Color(0xFF5E1607),
    secondaryContainer = Color(0xFF7A2D1B), onSecondaryContainer = Color(0xFFFFDAD0),
    tertiary = Color(0xFFFFD36A), onTertiary = Color(0xFF422C00),
    background = Color(0xFF11130F), onBackground = Color(0xFFFFFCF4),
    surface = Color(0xFF1A1D18), onSurface = Color(0xFFFFFCF4),
    surfaceVariant = Color(0xFF30352D), onSurfaceVariant = Color(0xFFE8EDE2),
    outline = Color(0xFFBFC6B8),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
)

@Composable
fun NutritionGuesserTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
