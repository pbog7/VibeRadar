package com.pbogdev.ephemeralmessaging.messagingScreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbogdev.core.appLogger
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.EphemeralMessage
import com.pbogdev.domain.usecase.DecryptEphemeralMessageUseCase
import com.pbogdev.domain.usecase.DeleteEphemeralMessageUseCase
import com.pbogdev.domain.usecase.GetUserBeaconUseCase
import com.pbogdev.domain.usecase.SendEphemeralMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class EphemeralMessagingViewModel(
    private val sendEphemeralMessageUseCase: SendEphemeralMessageUseCase,
    private val deleteEphemeralMessageUseCase: DeleteEphemeralMessageUseCase,
    private val decryptEphemeralMessageUseCase: DecryptEphemeralMessageUseCase,
    private val getUserBeaconUseCase: GetUserBeaconUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(EphemeralMessagingViewState())
    val viewState: StateFlow<EphemeralMessagingViewState> = _viewState.asStateFlow()

    val messageInputState = TextFieldState()

    fun initializeSession(
        targetMatchmakingBeacon: MatchmakingBeacon,
        encryptedEphemeralMessage: String?
    ) {
        viewModelScope.launch {
            val getUserBeaconResult = getUserBeaconUseCase()
            if (getUserBeaconResult is CustomResult.Success) {
                _viewState.update {
                    it.copy(
                        targetMatchmakingBeacon = targetMatchmakingBeacon,
                        myBeacon = getUserBeaconResult.data,
                        encryptedEphemeralMessage = encryptedEphemeralMessage,
                        messageState = if (encryptedEphemeralMessage != null) MessageState.INCOMING_MESSAGE else MessageState.DRAFTING
                    )
                }
            }
        }
    }

    fun sendEphemeralMessage() {
        if (messageInputState.text.isBlank()) return
        viewState.value.targetMatchmakingBeacon?.let { targetMatchmakingBeacon ->
            appLogger.d { "targetMatchmakingBeacon senderUid ${targetMatchmakingBeacon.beacon.senderUid}" }
            _viewState.update { it.copy(messageState = MessageState.ENCRYPTING_AND_SENDING) }
            viewModelScope.launch {
                val sendMessageResult = sendEphemeralMessageUseCase(
                    SendEphemeralMessageUseCase.Params(
                        ephemeralMessage = EphemeralMessage(
                            recipientBeaconId = targetMatchmakingBeacon.beacon.beaconId,
                            isOutgoing = true,
                            plainText = messageInputState.text.toString(),
                            expiresAt = targetMatchmakingBeacon.beacon.expiresAt,
                            recipientUid = targetMatchmakingBeacon.beacon.senderUid!!,
                            senderBeaconId = viewState.value.myBeacon!!.beaconId
                        ),
                        matchmakingBeacon = MatchmakingBeacon(
                            beacon = viewState.value.myBeacon!!,
                            matchResult = targetMatchmakingBeacon.matchResult
                        )
                    )
                )
                when (sendMessageResult) {
                    is CustomResult.Success -> {
                        messageInputState.clearText()
                        _viewState.update {
                            it.copy(messageState = MessageState.WAITING_FOR_REPLY)
                        }
                    }

                    is CustomResult.Failure -> {
                        _viewState.update {
                            it.copy(messageState = MessageState.DRAFTING)
                        }
                    }
                }
            }

        }
    }

    fun deleteMessage() {
        viewState.value.receivedEphemeralMessage?.let {
            viewModelScope.launch {
                deleteEphemeralMessageUseCase(
                    DeleteEphemeralMessageUseCase.Params(
                        messageId = it.messageId,
                        beaconId = viewState.value.targetMatchmakingBeacon!!.beacon.beaconId
                    )
                )
            }
        }
        _viewState.update { it.copy(receivedEphemeralMessage = null, messageState = MessageState.DRAFTING, encryptedEphemeralMessage = null) }
    }

    fun decryptMessage() {
        viewState.value.apply {
            targetMatchmakingBeacon?.let { targetMatchmakingBeacon ->
                encryptedEphemeralMessage?.let { encryptedMessage ->
                    viewModelScope.launch {
                        val decryptMessageResult = decryptEphemeralMessageUseCase(
                            DecryptEphemeralMessageUseCase.Params(
                                encryptedMessage = encryptedMessage,
                                targetBeaconId = targetMatchmakingBeacon.beacon.beaconId,
                                expiresAtEpochMillis = targetMatchmakingBeacon.beacon.expiresAt
                            )
                        )
                        if (decryptMessageResult is CustomResult.Success) {
                            _viewState.update {
                                it.copy(receivedEphemeralMessage = decryptMessageResult.data)
                            }
                        }
                    }
                }
            }
        }

    }

    fun clearState() {
        _viewState.update {
            EphemeralMessagingViewState()
        }
    }
}