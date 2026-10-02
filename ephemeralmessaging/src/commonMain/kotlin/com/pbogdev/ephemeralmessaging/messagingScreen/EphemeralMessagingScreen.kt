package com.pbogdev.ephemeralmessaging.messagingScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.Beacon
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EphemeralMessagingScreen(
    viewModel: EphemeralMessagingViewModel = koinViewModel(),
    targetMatchmakingBeacon: MatchmakingBeacon,
    onAbortSession: () -> Unit,
    encryptedEphemeralMessage: String?
) {
    val state by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.initializeSession(
            targetMatchmakingBeacon = targetMatchmakingBeacon,
            encryptedEphemeralMessage = encryptedEphemeralMessage
        )
    }
    DisposableEffect(Unit){
        onDispose {
            viewModel.clearState()
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Forces it to open fully expanded
    )
    val scope = rememberCoroutineScope()


    ModalBottomSheet(
        onDismissRequest = onAbortSession, // Triggered on swipe down or outside tap
        sheetState = sheetState,
        containerColor = Color.Black,
        scrimColor = Color.Transparent, // 1. Removes the dark background dimming
        dragHandle = null,              // 2. Removes the default grey pill
        shape = RectangleShape,         // 3. Or RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        modifier = Modifier.fillMaxSize()
    ) {
        EphemeralMessagingLayout(
            state = state,
            messageInputState = viewModel.messageInputState,
            sendMessage = viewModel::sendEphemeralMessage,
            onClose = {
                viewModel.clearState()
                // Smoothly hides the sheet before toggling visibility
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onAbortSession()
                }
            },
            onMessageDestroyed = viewModel::deleteMessage,
            onDecrypt = viewModel::decryptMessage
        )
    }
}