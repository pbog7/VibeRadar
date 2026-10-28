package com.pbogdev.homescreen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.getString
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.active_connection
import com.pbogdev.sharedui.active_connections_plural
import com.pbogdev.sharedui.unread_message
import com.pbogdev.sharedui.unread_messages_plural
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActiveConnectionPill(
    activeCount: Int,
    onClick: () -> Unit,
    unreadMessagesCount: Int
) {
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() },
        color = Color.Black,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing animation from your original Pill
            val infiniteTransition = rememberInfiniteTransition(label = "pillPulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                label = "alphaPulse"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alpha)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (unreadMessagesCount > 0) {
                Text(
                    text = when (unreadMessagesCount) {
                        1 -> stringResource(Res.string.active_connection)
                        else -> stringResource(Res.string.active_connections_plural, unreadMessagesCount)

                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.sp
                )
            } else {
                Text(
                    text = when (activeCount) {
                        1 -> stringResource(Res.string.unread_message)
                        else -> stringResource(Res.string.unread_messages_plural, activeCount)

                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.sp
                )
            }

        }
    }
}