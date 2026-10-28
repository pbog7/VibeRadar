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
fun TermsOfServiceScreen(
    onNavigateBack: () -> Unit,
    onAccept: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Core terminal background
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // --- HEADER ---
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
                text = "TERMS OF SERVICE",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- TERMS CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF121212)) // RadarBlack surface
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TermsSection(
                title = "01. THE ZERO-KNOWLEDGE CONTRACT",
                content = "Vibe Radar acts strictly as a \"Blind Relay\" network. Our backend functions as a zero-knowledge post office. We provide the cryptographic infrastructure to connect you, but you maintain absolute responsibility for the content you transmit."
            )

            TermsSection(
                title = "02. ACCEPTABLE USE",
                content = "You agree to use the Geohash matchmaking grid and E2EE messaging strictly for lawful discovery. You may not use this protocol to facilitate harassment, illegal commerce, or malicious cryptographic payloads. Violations will result in permanent device-level blacklisting."
            )

            TermsSection(
                title = "03. NO DATA RECOVERY",
                content = "By utilizing the Digital Shredder mechanism, you acknowledge that all data ephemerality is final. Once a message's Time-To-Live (TTL) expires or it is consumed, it is mathematically permanently destroyed. We cannot recover lost, shredded, or unsent intel under any circumstances."
            )

            TermsSection(
                title = "04. LOCALIZATION EXPOSURE",
                content = "While we protect your exact GPS coordinates, broadcasting a Matchmaking Beacon inherently signals your proximity within a 9-box precision-5 Geohash grid to other nearby users. You assume all physical risks associated with broadcasting your local presence."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "I ACCEPT THE TERMS",
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
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