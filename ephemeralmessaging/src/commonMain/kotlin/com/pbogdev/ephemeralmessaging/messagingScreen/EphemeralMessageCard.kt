package com.pbogdev.ephemeralmessaging.messagingScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@Composable
fun EphemeralMessageCard(
    decryptedMessage: String?,
    viewDurationSeconds: Int = 5,
    onMessageDestroyed: () -> Unit,
    modifier: Modifier = Modifier,
    onDecrypt: () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(viewDurationSeconds) }

    // The Digital Shredder trigger
    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            val startMark = TimeSource.Monotonic.markNow()
            val timeout = viewDurationSeconds.seconds
            while (true) {
                val elapsed = startMark.elapsedNow()
                val remaining = (timeout - elapsed).inWholeSeconds.toInt()

                // Triggers destruction based on absolute time, not UI ticks
                if (remaining <= 0) {
                    timeLeft = 0
                    onMessageDestroyed()
                    break
                }

                // Only trigger a UI recomposition when the actual second changes
                if (timeLeft != remaining) {
                    timeLeft = remaining
                }

                // A tight delay ensures the loop catches the zero-mark almost instantly,
                // even if returning from the background
                delay(100L)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background) // RadarBlack
            .border(
                width = 1.dp,
                color = if (isRevealed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, // AlertRed vs NeonGreen
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isRevealed) {
                isRevealed = true
                onDecrypt()
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isRevealed,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "RevealAnimation"
        ) { revealed ->
            if (!revealed || decryptedMessage == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ENCRYPTED PAYLOAD",
                        color = MaterialTheme.colorScheme.primary, // NeonGreen
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap to view. Message will be destroyed in ${viewDurationSeconds}s.",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Countdown Text (Top)
                    val timeString = timeLeft.toString().padStart(2, '0')
                    Text(
                        text = "Message will be destroyed in 00:$timeString...",
                        color = MaterialTheme.colorScheme.error, // AlertRed
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // 2. Decrypted Message (Middle)
                    Text(
                        text = decryptedMessage,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // 3. Destroy Button (Bottom)
                    Text(
                        text = "DESTROY NOW",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onMessageDestroyed() }
                            .padding(top = 8.dp, bottom = 4.dp, end = 8.dp) // Slight padding adjustments for touch target
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EphemeralRevealCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(24.dp)
        ) {
            EphemeralMessageCard(
                decryptedMessage = "Target location confirmed. Commencing extraction.",
                viewDurationSeconds = 5,
                onDecrypt = {},
                onMessageDestroyed = {
                    println("Message destroyed callback triggered.")
                }
            )
        }
    }
}