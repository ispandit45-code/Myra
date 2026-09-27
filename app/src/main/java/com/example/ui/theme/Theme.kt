package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1F1400),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldSecondary,
    secondary = GoldSecondary,
    onSecondary = Color(0xFF261900),
    secondaryContainer = LuxurySurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonSpeedGreen,
    onTertiary = Color(0xFF002917),
    background = LuxuryBackground,
    onBackground = TextPrimary,
    surface = LuxurySurface,
    onSurface = TextPrimary,
    surfaceVariant = LuxurySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = LuxuryBorder,
    outlineVariant = LuxuryBorderGold
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
