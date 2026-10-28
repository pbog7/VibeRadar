package com.pbogdev.settingscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pbogdev.sharedui.close_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsMenuLayout(
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.close_icon), // Assuming a close or back icon
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Close Settings"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.primary, // NeonGreen
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SETTINGS OPTIONS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Tightened spacing since items are smaller
        ) {
            SettingsItem(
                title = "PRIVACY POLICY",
                onClick = onNavigateToPrivacy
            )

            Spacer(modifier = Modifier.width(16.dp).height(16.dp))

//            HorizontalDivider(
//                modifier = Modifier.padding(vertical = 8.dp),
//                color = Color.DarkGray.copy(alpha = 0.3f)
//            )

            SettingsItem(
                title = "TERMS OF SERVICE",
                onClick = onNavigateToTerms
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}