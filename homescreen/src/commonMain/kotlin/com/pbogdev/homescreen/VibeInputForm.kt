package com.pbogdev.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.sharedui.theme.VibeRadarTheme
import kotlinx.coroutines.launch

@Composable
fun VibeInputForm(
    onStartSearch: () -> Unit,
    vibeState: TextFieldState,
    likesState: TextFieldState,
    dislikesState: TextFieldState,
    beaconUploaded: Boolean,
    deleteBeacon: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .background(Color.Black.copy(alpha = 0f), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // You can replace these with your actual TextField components
        Text(
            text = if(!beaconUploaded)"What's your vibe?" else "Active Beacon",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        StealthTextField(
            enabled = !beaconUploaded,
            state = vibeState,
            label = "Vibe",
            fieldBackground = MaterialTheme.colorScheme.surface
        )
        Spacer(modifier = Modifier.height(16.dp))
        StealthTextField(
            enabled = !beaconUploaded,
            state = likesState,
            label = "Likes",
            fieldBackground = MaterialTheme.colorScheme.surface
        )
        Spacer(modifier = Modifier.height(16.dp))

        StealthTextField(
            enabled = !beaconUploaded,
            state = dislikesState,
            label = "Dislikes",
            fieldBackground = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    onStartSearch()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            enabled = vibeState.text.isNotBlank(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if(!beaconUploaded)"Upload Beacon and scan" else "Scan",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    deleteBeacon()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = beaconUploaded,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Delete Beacon",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun StealthTextField(
    state: TextFieldState,
    label: String,
    fieldBackground: Color,
    enabled: Boolean
) {
    OutlinedTextField(
        state = state,
        enabled = enabled,
        inputTransformation = InputTransformation.maxLength(200),
        label = { Text(label) },
        labelPosition = TextFieldLabelPosition.Above(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        // Still enforcing our strict local-only privacy rule
        keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = fieldBackground,
            unfocusedContainerColor = fieldBackground,
            disabledContainerColor = fieldBackground,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            // Completely transparent borders to blend into the background
            focusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            unfocusedBorderColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            cursorColor = MaterialTheme.colorScheme.onSurface,
            errorBorderColor = MaterialTheme.colorScheme.error,
        )
    )
}

@Preview() // Pure black background
@Composable
fun VibeInputFormPreview() {
    // 1. Mock the memory buffers with some dummy data
    val mockVibeState = rememberTextFieldState("Looking for a quiet spot to code.")
    val mockLikesState = rememberTextFieldState("Coffee, Lo-Fi, Rain")
    val mockDislikesState = rememberTextFieldState("Loud music, Crowds")
    var mockBeaconUploaded = true

    // 2. Wrap in your theme and a mock Radar background
    VibeRadarTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter,
        ) {
            RadarBackground(isAnimated = false, modifier = Modifier.fillMaxSize())
            // 3. Render the stateless component
            VibeInputForm(
                vibeState = mockVibeState,
                likesState = mockLikesState,
                dislikesState = mockDislikesState,
                onStartSearch = {
                    // Mock the suspend result

                    CustomResult.Success(Unit)
                },
                beaconUploaded = mockBeaconUploaded,
                deleteBeacon = {
                    mockVibeState.clearText()
                    mockLikesState.clearText()
                    mockDislikesState.clearText()
                }
            )
        }
    }
}