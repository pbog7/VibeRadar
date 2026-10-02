package com.pbogdev.sharedui.components.vibeListOverlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.MatchmakingResult
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.ConnectionStatus
import com.pbogdev.sharedui.theme.VibeRadarTheme

@Composable
fun VibeListOverlay(
    matches: List<MatchmakingBeacon>,
    sessionStatuses: Map<String, ConnectionStatus>? = null,
    title: String,
    actionButtonText: String,
    onClose: () -> Unit = {},
    onConnect: (target: MatchmakingBeacon) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                top = 48.dp,
                bottom = 32.dp,
                end = 4.dp,
                start = 4.dp
            )// Slightly reduced outer padding for list layout
            .background(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sticky Header
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable List of Matches
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
            // Prevents the overlay from covering the whole screen
        ) {
            items(matches) { match ->
                MatchItem(
                    match = match,
                    onAction = onConnect,
                    actionButtonText = actionButtonText,
                    connectionStatus = sessionStatuses?.get(match.beacon.beaconId)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- STICKY FOOTER BUTTON ---
        OutlinedButton(
            onClick = { onClose()  },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Dismiss", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview
@Composable
fun PreviewVibeListOverlay() {
    VibeRadarTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Dark background simulator
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter // Pins it to the bottom like the real app
        ) {
            VibeListOverlay(
                title = "",
                actionButtonText = "",
                onConnect = {},
                onClose = {}, matches = listOf(
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-1",
                            vibeVector = FloatArray(512) { 0.1f },
                            vibe = "Love building low-level cryptographic pipelines. Looking for someone who appreciates efficient code and quiet coffee shops.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.98f,
                            dislikesMatchScore = 0.05f,
                            vibeMatchScore = 0.95f,
                            overallMatchScore = 0.92f
                        )
                    ),
                    MatchmakingBeacon(
                        beacon = Beacon(
                            beaconId = "b7-2",
                            vibeVector = FloatArray(512) { 0.2f },
                            vibe = "Just vibing and looking for new connections in the city.",
                            expiresAt = 1750000000000L
                        ),
                        matchResult = MatchmakingResult(
                            likesMatchScore = 0.60f,
                            dislikesMatchScore = 0.20f,
                            vibeMatchScore = 0.70f,
                            overallMatchScore = 0.65f
                        )
                    )
                )
            )
        }
    }

}