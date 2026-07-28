package com.pbogdev.homescreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.MatchmakingResult
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.Profile
import com.pbogdev.sharedui.theme.VibeRadarTheme
import kotlin.time.Clock.System

@Composable
fun MatchItem(match: MatchmakingBeacon, onConnect:()-> Unit) {
    var isExpanded by rememberSaveable(match.beacon.beaconId) { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clickable { isExpanded = !isExpanded }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Subtle neon border
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() // Animates the height change smoothly
                .padding(16.dp)
        ) {
            // Header: Always visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.matchResult.overallMatchScore*100}% Match",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isExpanded) "Less ▲" else "More ▼",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                val profile = match.beacon.profile
                if (match.beacon.vibe.isNotBlank()) {
                    Text(
                        text = "Vibe",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = match.beacon.vibe,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!profile?.likes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Likes",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.likes,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onConnect,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    ),
                ) {
                    Text(text = "CONNECT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMatchItem() {
    val dummyVector = FloatArray(512) { 0.1f }
    val dummyMatchmakingBeacon = MatchmakingBeacon(
        beacon = Beacon(
            beaconId = "beacon-1",
            vibeVector = dummyVector,
            vibe = "Looking for someone to grab artisan coffee and debate Clean Architecture patterns.",
            expiresAt = System.now().toEpochMilliseconds()+ 86400000L, // time now +1 day
            profile = Profile(
                id = "prof-1",
                likes = "Kotlin, Coffee, Dark Mode, Mechanical Keyboards",
                dislikes = "Spaghetti code, loud spaces",
                likesVector = dummyVector,
                dislikesVector = dummyVector
            )
        ),
        matchResult = MatchmakingResult(0.95f, 0.05f, 0.98f, 0.96f)
    )
    VibeRadarTheme {
        Box(Modifier.fillMaxSize()){
            MatchItem(match = dummyMatchmakingBeacon, onConnect = {})
        }
    }
}