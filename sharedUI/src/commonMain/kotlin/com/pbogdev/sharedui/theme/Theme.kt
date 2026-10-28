package com.pbogdev.sharedui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VibeRadarColorScheme = darkColorScheme(
    // 🟢 Primary branding (Buttons, Active Radar Sweeps, Sliders)
    primary = NeonGreen,
    onPrimary = RadarBlack, // Black text on green buttons for max readability

    // ⚫ Backgrounds (The main map/radar screen)
    background = RadarBlack,
    onBackground = TextWhite, // White text directly on the black background

    // 🔘 Surfaces (The expandable Match Overlay from the bottom)
    surface = RadarSurface,
    onSurface = TextWhite,

    // 🔘 Surface Variants (Text input fields, collapsed match cards)
    surfaceVariant = RadarSurfaceVariant,
    onSurfaceVariant = TextGray, // Softer text for secondary info (Shared Interests)

    // 🔴 Errors (Invalid text input, network failures)
    error = AlertRed,
    onError = RadarBlack,

    // Optional: Secondary color if you want to use the Dim Green for subtle accents
    secondary = NeonGreenDim,
    onSecondary = NeonGreen
)

@Composable
fun VibeRadarTheme(
    // We ignore the darkTheme parameter entirely to force the dark radar UI
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VibeRadarColorScheme,
        typography = getVibeTypography(), // From our previous Typography setup
        content = content
    )
}