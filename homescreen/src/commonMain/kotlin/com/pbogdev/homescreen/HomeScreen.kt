package com.pbogdev.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pbogdev.core.appLogger
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.permissions.rememberLocationPermissionHandler
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.settings_24px
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    openSettings: () -> Unit
) {
    val state by viewModel.viewState.collectAsState()
    val scope = rememberCoroutineScope()
    appLogger.i { "Home Screen composition" }
    val locationPermission = rememberLocationPermissionHandler { isGranted ->
        if (isGranted) {
            scope.launch {
                val findMatchResult = viewModel.findMatch()
                if (findMatchResult is CustomResult.Failure) {
                    appLogger.i { "FindMatch error ${findMatchResult.error.message}" }
                    // show error
                }
            }
        } else {
            appLogger.i { "Permissions denied" }
            // show error
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()// Dark sleek background
    ) {

        RadarBackground(
            isAnimated = state.radarState == RadarState.SEARCHING,
            modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp)
        )

        AnimatedVisibility(
            visible = state.radarState == RadarState.IDLE,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            VibeInputForm(
                onStartSearch = { locationPermission.requestPermission() },
                vibeState = viewModel.vibeState,
                likesState = viewModel.likesState,
                dislikesState = viewModel.dislikesState,
                beaconUploaded = !state.uploadNewBeacon,
                deleteBeacon = viewModel::deleteBeacon
            )
        }

        AnimatedVisibility(
            visible = state.radarState == RadarState.MATCHES && !state.nearbyBeacons.isNullOrEmpty(),
            enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
            exit = slideOutVertically(
                targetOffsetY = { it }, // Slides down completely off-screen
                animationSpec = tween(200, easing = FastOutLinearInEasing)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            state.nearbyBeacons?.let {
                MatchResultsOverlay(
                    matches = it,
                    onClose = { viewModel.setRadarState(RadarState.IDLE) })
            }

        }
        IconButton(
            onClick = { openSettings() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.settings_24px),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = "Open Settings"
            )
        }
    }
}

