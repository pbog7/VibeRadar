package com.pbogdev.ephemeralmessaging.messagingScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.MatchmakingResult
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.EphemeralMessage
import com.pbogdev.domain.models.Profile
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.close_icon
import com.pbogdev.sharedui.messaging_icon
import com.pbogdev.sharedui.settings_24px
import com.pbogdev.sharedui.theme.VibeRadarTheme
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock.System


@Composable
fun EphemeralMessagingLayout(
    state: EphemeralMessagingViewState,
    messageInputState: TextFieldState,
    sendMessage: () -> Unit,
    onClose: () -> Unit,
    onMessageDestroyed: () -> Unit,
    onDecrypt: () -> Unit
) {

    var isIntelExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {} // Absorbs background taps
            )
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                val matchPercentage =
                    state.targetMatchmakingBeacon?.matchResult?.overallMatchScore?.times(
                        100f
                    )?.toInt() ?: 0
                Text(
                    text = "${matchPercentage}% MATCH",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    letterSpacing = 1.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(Res.drawable.close_icon),
                    tint = Color.Gray,
                    contentDescription = "Cancel Draft",
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.messageState == MessageState.DRAFTING) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D0D12))
                    .border(1.dp, Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { isIntelExpanded = !isIntelExpanded }
                    .animateContentSize(animationSpec = tween(300, easing = LinearOutSlowInEasing))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIBE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = if (isIntelExpanded) "Less ▲" else "More ▼",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                state.targetMatchmakingBeacon?.beacon?.let { targetBeacon ->
                    Text(
                        text = targetBeacon.vibe,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (isIntelExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isIntelExpanded && (!targetBeacon.profile?.likes.isNullOrBlank() || !targetBeacon.profile?.dislikes.isNullOrBlank())) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (!targetBeacon.profile?.likes.isNullOrBlank()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "LIKES",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = targetBeacon.profile!!.likes,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            if (!targetBeacon.profile?.dislikes.isNullOrBlank()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "DISLIKES",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = targetBeacon.profile!!.dislikes!!,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedContent(
            targetState = state.messageState,
            label = "MessageStateTransition",
            modifier = Modifier.weight(1f)
        ) { messageState ->
            when (messageState) {
                MessageState.DRAFTING -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        // --- SECURITY TRUST BADGE ---
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.settings_24px), // Replace with a Lock icon
                                tint = Color.Gray,
                                contentDescription = "Encrypted",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "END-TO-END ENCRYPTED • BURNS AFTER VIEWING",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp
                            )
                        }

                        // --- MESSAGE INPUT ---
                        OutlinedTextField(
                            state = messageInputState,
                            placeholder = {
                                Text(
                                    "Type a secure message...",
                                    color = Color.DarkGray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 200.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFF0D0D12),
                                focusedContainerColor = Color(0xFF0D0D12),
                                unfocusedBorderColor = Color.DarkGray.copy(alpha = 0.5f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- ACTION ROW ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // SEND BUTTON - Now points to the Encrypting State for testing
                            Button(
                                onClick = sendMessage,
                                enabled = messageInputState.text.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = Color.DarkGray.copy(alpha = 0.2f),
                                    disabledContentColor = Color.Gray
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (messageInputState.text.isNotBlank()) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.5f
                                    ) else Color.Transparent
                                )
                            ) {
                                Text("SEND", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }

                MessageState.ENCRYPTING_AND_SENDING -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "SECURING & SENDING",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please remain on this screen.\nTransmission will complete in a few seconds.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }

                MessageState.WAITING_FOR_REPLY -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alphaPulse"
                        )

                        Icon(
                            painter = painterResource(Res.drawable.messaging_icon),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Waiting",
                            modifier = Modifier
                                .size(64.dp)
                                .alpha(alpha)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "MESSAGE SENT",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Awaiting target connection.\nYou may close this channel.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                MessageState.INCOMING_MESSAGE -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EphemeralMessageCard(
                                decryptedMessage = state.receivedEphemeralMessage?.plainText, // Feed your decrypted state here
                                viewDurationSeconds = 30,
                                onMessageDestroyed = onMessageDestroyed,
                                onDecrypt = onDecrypt
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.settings_24px), // Replace with a Lock icon
                                tint = Color.Gray,
                                contentDescription = "Encrypted",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "END-TO-END ENCRYPTED • BURNS AFTER VIEWING",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp
                            )
                        }

                        // --- MESSAGE INPUT ---
                        OutlinedTextField(
                            state = messageInputState,
                            placeholder = {
                                Text(
                                    "Type a secure message...",
                                    color = Color.DarkGray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 200.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFF0D0D12),
                                focusedContainerColor = Color(0xFF0D0D12),
                                unfocusedBorderColor = Color.DarkGray.copy(alpha = 0.5f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Drafting State",
    showBackground = true,
    backgroundColor = 0xFF000000,
)
@Composable
fun PreviewEphemeralMessagingLayout() {
    val dummyVector = FloatArray(512) { 0.1f }
    val futureTime = System.now().toEpochMilliseconds() + 86400000L // +1 day
    val viewState = EphemeralMessagingViewState(
        messageState = MessageState.INCOMING_MESSAGE,
        encryptedEphemeralMessage = "asdasdsad",
        receivedEphemeralMessage = EphemeralMessage(
            messageId = "1",
            recipientUid = "1",
            recipientBeaconId = "1",
            expiresAt = futureTime,
            isOutgoing = false,
            plainText = "This is an encrypted message",
            senderBeaconId = "2"
        ),
        targetMatchmakingBeacon = MatchmakingBeacon(
            beacon = Beacon(
                beaconId = "beacon-6",
                vibeVector = dummyVector,
                vibe = "Going to the loud, overcrowded club downtown.",
                expiresAt = futureTime,
                profile = Profile(
                    id = "prof-6",
                    likes = "EDM, VIP sections, Bottle service",
                    dislikes = "Quiet nights in, reading, cats",
                    likesVector = dummyVector,
                    dislikesVector = dummyVector
                )
            ),
            matchResult = MatchmakingResult(0.75f, 0.10f, 0.80f, 0.78f)
        )
    )
    val messagingState = TextFieldState()
    VibeRadarTheme {
        EphemeralMessagingLayout(
            state = viewState,
            messageInputState = messagingState,
            sendMessage = { },
            onClose = {},
            onDecrypt = {},
            onMessageDestroyed = {}
        )
    }
}