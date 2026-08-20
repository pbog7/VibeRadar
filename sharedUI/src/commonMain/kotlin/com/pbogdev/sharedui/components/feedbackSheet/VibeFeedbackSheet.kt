package com.pbogdev.sharedui.components.feedbackSheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pbogdev.sharedui.theme.AlertAmber
import com.pbogdev.sharedui.theme.AlertRed
import com.pbogdev.sharedui.theme.InfoBlue
import com.pbogdev.sharedui.theme.NeonGreen
import com.pbogdev.sharedui.theme.VibeRadarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibeFeedbackSheet(
    feedbackConfig: FeedbackConfig,
    onDismiss: () -> Unit = {}
) {
    // 1. Dynamically resolve the semantic color based on the feedback type
    val semanticColor = when (feedbackConfig.feedbackType) {
        FeedbackType.ERROR -> AlertRed
        FeedbackType.WARNING -> AlertAmber
        FeedbackType.INFO -> InfoBlue
        FeedbackType.SUCCESS -> NeonGreen
    }

    // 2. Configure the sheet to only snap to the bottom (no half-expansion)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface, // Seamlessly matches your VibeInputForm
        contentColor = semanticColor,
        // Tint the drag handle to match the semantic state
        dragHandle = { BottomSheetDefaults.DragHandle(color = semanticColor.copy(alpha = 0.5f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                // Add safe area padding for devices with gesture bars
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -- TITLE --
            Text(
                text = feedbackConfig.title.uppercase(), // Force terminal aesthetic
                style = MaterialTheme.typography.titleLarge,
                color = semanticColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -- MESSAGE --
            Text(
                text = feedbackConfig.message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f), // High readability contrast
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // -- PRIMARY ACTION BUTTON (Optional) --
            feedbackConfig.primaryActionLabel?.let { label ->
                Button(
                    onClick = {
                        feedbackConfig.onPrimaryAction?.invoke()
                        onDismiss() // Automatically close sheet when action is taken
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        // Create a "ghost button" effect using the semantic color
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp), // Matches the primary button exactly
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Keeps it secondary
                    contentColor = Color.White.copy(alpha = 0.6f) // Muted text
                ),
                shape = RoundedCornerShape(12.dp), // Matches the primary button
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) // Very subtle border
            ) {
                Text(
                    text = "DISMISS",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Feedback Sheet - ERROR",
    showBackground = true,
    backgroundColor = 0xFF000000 // Matches RadarBlack for accurate contrast
)
@Composable
private fun VibeFeedbackSheetErrorPreview() {
    VibeRadarTheme { // Forces the VibeRadarColorScheme and Typography
        Box(Modifier.fillMaxSize()){
            VibeFeedbackSheet(
                feedbackConfig = FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = "SIGNAL LOST",
                    message = "The Blind Relay network timed out. Check your connection and try again.",
                    primaryActionLabel = "RETRY SCAN",
                    onPrimaryAction = {}
                ),
                onDismiss = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Feedback Sheet - SUCCESS",
    showBackground = true,
    backgroundColor = 0xFF000000 // Matches RadarBlack
)
@Composable
private fun VibeFeedbackSheetSuccessPreview() {
    VibeRadarTheme {
        Box(Modifier.fillMaxSize()){
            VibeFeedbackSheet(
                feedbackConfig = FeedbackConfig(
                    feedbackType = FeedbackType.SUCCESS,
                    title = "BEACON DROPPED",
                    message = "Your vibe is now encrypted and broadcasting to the local grid.",
                    primaryActionLabel = "VIEW MAP",
                    onPrimaryAction = {}
                ),
                onDismiss = {}
            )
        }
    }
}
