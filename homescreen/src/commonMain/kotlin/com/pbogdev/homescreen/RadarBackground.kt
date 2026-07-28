package com.pbogdev.homescreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import com.pbogdev.sharedui.theme.VibeRadarTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarBackground(
    isAnimated: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Continuous clockwise sweep angle rotation (0 to 360 degrees)
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2
        val radarColor = primaryColor.copy(alpha = 0.3f)// Neon green

        // 1. Draw Static Rings & Grid Lines
        drawCircle(
            color = radarColor,
            radius = maxRadius * 0.33f,
            center = center,
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = radarColor,
            radius = maxRadius * 0.66f,
            center = center,
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = radarColor,
            radius = maxRadius,
            center = center,
            style = Stroke(width = 2f)
        )

        val centerGap = 24f // Leaves a clean empty space in the exact center

        // Top line
        drawLine(
            color = radarColor,
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y - centerGap),
            strokeWidth = 2f
        )

        // Bottom line
        drawLine(
            color = radarColor,
            start = Offset(center.x, center.y + centerGap),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 2f
        )

        // Left line
        drawLine(
            color = radarColor,
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x - centerGap, center.y),
            strokeWidth = 2f
        )

        // Right line
        drawLine(
            color = radarColor,
            start = Offset(center.x + centerGap, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 2f
        )

        // 2. Draw Rotating Radar Needle Sweep
        if (isAnimated) {
            // Draw a trailing gradient arc behind the leading needle
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        radarColor.copy(alpha = 0.6f), // Leading bright color
                        radarColor.copy(alpha = 0.0f)  // Fading tail trailing behind
                    ),
                    center = center
                ),
                startAngle = sweepAngle - 45f, // Controls how long the sweep tail is
                sweepAngle = 45f,
                useCenter = true,
                size = Size(maxRadius * 2, maxRadius * 2),
                topLeft = Offset(center.x - maxRadius, center.y - maxRadius)
            )

            // Draw the sharp leading edge line ("The Needle")
            val angleInRadians = sweepAngle * (PI.toFloat() / 180f)
            val needleEnd = Offset(
                x = (center.x + maxRadius * cos(angleInRadians)),
                y = (center.y + maxRadius * sin(angleInRadians))
            )
            drawLine(
                color = radarColor.copy(alpha = 0.8f),
                start = center,
                end = needleEnd,
                strokeWidth = 3f
            )
        }
        drawCircle(
            color = radarColor.copy(alpha = if(isAnimated) 0.9f else 0.6f), // High opacity to stand out
            radius = 12f, // Small, sharp dot
            center = center,
            style = Fill // Solid fill instead of a stroke
        )
    }
}

@Preview
@Composable
fun PreviewRadarStatic() {
    // Wrapped in our dark theme color so the neon pops
    VibeRadarTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            RadarBackground(
                isAnimated = false,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Preview
@Composable
fun PreviewRadarAnimated() {
    VibeRadarTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            RadarBackground(
                isAnimated = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}