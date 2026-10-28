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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pbogdev.core.appLogger
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.permissions.rememberLocationPermissionHandler
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.authentication_error_message
import com.pbogdev.sharedui.authentication_error_title
import com.pbogdev.sharedui.blank_vibe_error_message
import com.pbogdev.sharedui.blank_vibe_error_title
import com.pbogdev.sharedui.components.feedbackSheet.FeedbackConfig
import com.pbogdev.sharedui.components.feedbackSheet.FeedbackType
import com.pbogdev.sharedui.components.feedbackSheet.VibeFeedbackSheet
import com.pbogdev.sharedui.components.vibeListOverlay.VibeListOverlay
import com.pbogdev.sharedui.decryption_error_message
import com.pbogdev.sharedui.decryption_error_title
import com.pbogdev.sharedui.empty_beacon_list_error_message
import com.pbogdev.sharedui.empty_beacon_list_error_title
import com.pbogdev.sharedui.encryption_error_message
import com.pbogdev.sharedui.encryption_error_title
import com.pbogdev.sharedui.geohash_error_message
import com.pbogdev.sharedui.geohash_error_title
import com.pbogdev.sharedui.location_permissions_error_message
import com.pbogdev.sharedui.location_permissions_error_title
import com.pbogdev.sharedui.network_error_message
import com.pbogdev.sharedui.network_error_title
import com.pbogdev.sharedui.retry
import com.pbogdev.sharedui.settings_24px
import com.pbogdev.sharedui.text_embedding_error_message
import com.pbogdev.sharedui.text_embedding_error_title
import com.pbogdev.sharedui.unknown_error_message
import com.pbogdev.sharedui.unknown_error_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    openSettings: () -> Unit,
    openSystemSettings: () -> Unit,
    onConnect: (target: MatchmakingBeacon) -> Unit,
    onActiveConnectionSelected: (activeConnection: ActiveConnection) -> Unit
) {
    val state by viewModel.viewState.collectAsState()
    val scope = rememberCoroutineScope()
    var feedbackConfig by remember { mutableStateOf<FeedbackConfig?>(null) }
    val permissionDeniedTitle =
        stringResource(Res.string.location_permissions_error_title) // e.g. "Permission Denied"
    val permissionDeniedMessage = stringResource(Res.string.location_permissions_error_message)
    var isSessionSelectorOpen by remember { mutableStateOf(false) }
    val displayMatches = remember(state.activeConnectionsList) {
        state.activeConnectionsList.map { it.target }
    }
    val displayStatuses = remember(state.activeConnectionsList) {
        state.activeConnectionsList.associateBy(
            keySelector = { it.target.beacon.beaconId },
            valueTransform = { it.status }
        )
    }
    val unreadMessagesCount = remember(state.activeConnectionsList) {
        state.activeConnectionsList.filter { it.encryptedUnreadMessage != null }.size
    }
    appLogger.i { "Home Screen composition" }

    val locationPermission = rememberLocationPermissionHandler { isGranted ->
        if (isGranted) {
            executeFindMatch(
                scope = scope,
                findMatch = viewModel::findMatch,
                onFeedback = { feedbackConfig = it })
        } else {
            appLogger.i { "Permissions denied" }

            feedbackConfig = FeedbackConfig(
                feedbackType = FeedbackType.ERROR, // Uses AlertRed to signal a blocking issue
                title = permissionDeniedTitle,
                message = permissionDeniedMessage,
                primaryActionLabel = "OPEN SETTINGS",
                onPrimaryAction = openSystemSettings
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()// Dark sleek background
    ) {
        AnimatedVisibility(
            visible = state.activeConnectionsList.isNotEmpty() && !isSessionSelectorOpen,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        ) {
            ActiveConnectionPill(
                activeCount = state.activeConnectionsList.size,
                onClick = {
                    if (state.activeConnectionsList.size == 1) {
                        onActiveConnectionSelected(state.activeConnectionsList.first())
                    } else {
                        isSessionSelectorOpen = true
                    }
                },
                unreadMessagesCount = unreadMessagesCount
            )
        }

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
                VibeListOverlay(
                    matches = it,
                    onConnect = onConnect,
                    onClose = { viewModel.setRadarState(RadarState.IDLE) },
                    title = "${it.size} matches found",
                    actionButtonText = "connect"
                )
            }

        }
        AnimatedVisibility(
            visible = isSessionSelectorOpen,
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
            VibeListOverlay(
                matches = displayMatches,
                sessionStatuses = displayStatuses,
                onConnect = onConnect,
                onClose = {
                    isSessionSelectorOpen = false
                    viewModel.setRadarState(RadarState.IDLE)
                },
                title = "${state.activeConnectionsList.size} matches found",
                actionButtonText = "connect"
            )


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
    feedbackConfig?.let { config ->
        VibeFeedbackSheet(
            feedbackConfig = config
        )
    }
}

fun executeFindMatch(
    scope: CoroutineScope,
    findMatch: suspend () -> CustomResult<Unit>, // Or the specific return type of findMatch
    onFeedback: (FeedbackConfig?) -> Unit
) {
    scope.launch {
        onFeedback(null)
        val findMatchResult = findMatch()
        if (findMatchResult is CustomResult.Failure) {
            appLogger.i { "FindMatch error ${findMatchResult.error.message}" }
            // show error
            val retryAction: () -> Unit = { executeFindMatch(scope, findMatch, onFeedback) }

            val config = when (findMatchResult.error) {
                is CustomError.BlankTextError -> FeedbackConfig(
                    feedbackType = FeedbackType.WARNING,
                    title = getString(Res.string.blank_vibe_error_title),
                    message = getString(Res.string.blank_vibe_error_message)
                )

                is CustomError.EmptyList -> FeedbackConfig(
                    feedbackType = FeedbackType.INFO,
                    title = getString(Res.string.empty_beacon_list_error_title),
                    message = getString(Res.string.empty_beacon_list_error_message),
                )

                // Network Error
                is CustomError.NetworkError -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.network_error_title),
                    message = getString(Res.string.network_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = retryAction
                )


                // Not sure if these are properly handled like this, should probably make changes later
                // Cryptography and AI engine errors

                is CustomError.EncryptionError -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.encryption_error_title),
                    message = getString(Res.string.encryption_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = {}
                )

                is CustomError.DecryptionError -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.decryption_error_title),
                    message = getString(Res.string.decryption_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = {}
                )

                is CustomError.TextEmbedderInitializationError,
                is CustomError.EmbeddingError -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.text_embedding_error_title),
                    message = getString(Res.string.text_embedding_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = {} // Or potentially null if a hard restart is needed
                )

                // Authentication errors
                is CustomError.SignInError,
                is CustomError.GetTokenError,
                is CustomError.UserNotLoggedIn -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.authentication_error_title),
                    message = getString(Res.string.authentication_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = {}
                )

                // Geohash and Location errors
                is CustomError.GeohashNotStored, is CustomError.LocationUnavailable -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.geohash_error_title),
                    message = getString(Res.string.geohash_error_message),
                    primaryActionLabel = getString(Res.string.retry),
                    onPrimaryAction = retryAction
                )
                // General error fallback
                else -> FeedbackConfig(
                    feedbackType = FeedbackType.ERROR,
                    title = getString(Res.string.unknown_error_title),
                    message = getString(Res.string.unknown_error_message)
                )
            }
            onFeedback(config)
        }
    }
}



