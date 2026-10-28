package com.pbogdev.settingscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.chevron_left
import org.jetbrains.compose.resources.painterResource

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Foundation for the dark terminal aesthetic
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // --- MINIMALIST HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left),
                    tint = Color.White,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "PRIVACY POLICY",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- POLICY CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PolicySection(
                title = "01. ZERO-KNOWLEDGE ARCHITECTURE",
                content = "Vibe Radar operates on a \"Blind Relay\" architecture. Our backend servers act as a zero-knowledge post office, meaning we cannot read, parse, or monetize your private messages."
            )

            PolicySection(
                title = "02. HARDWARE-LEVEL LOCATION PRIVACY",
                content = "We do not track your exact GPS coordinates. Your location is reduced to a coarse, precision-5 Geohash grid on your physical device. We only query this generalized grid to find nearby active connections, ensuring your exact whereabouts remain mathematically obscured."
            )

            PolicySection(
                title = "03. ON-DEVICE AI & MATCHMAKING",
                content = "Your personality profile, likes, dislikes, and \"vibes\" are processed using local AI text embeddings (MediaPipe). Semantic matching and cosine similarity algorithms run entirely on your local hardware. We do not transmit your raw personality vectors to third-party AI cloud services."
            )

            PolicySection(
                title = "04. END-TO-END ENCRYPTED (E2EE) MESSAGES",
                content = "All direct messages are End-to-End Encrypted using AES-GCM and HKDF cryptographic derivation. The decryption keys are tied to ephemeral session IDs and are never transmitted to or stored on our servers."
            )

            PolicySection(
                title = "05. THE DIGITAL SHREDDER",
                content = "Vibe Radar enforces strict data ephemerality. Messages are bound by a Time-To-Live (TTL) countdown. Once a message is read, or its TTL expires, our Digital Shredder permanently destroys the cryptographic payload from both your local Room database and our backend Firestore relays."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Last Updated: September 2026\nBy using Vibe Radar, you agree to this privacy-first protocol.",
                color = Color.DarkGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )
    }
}