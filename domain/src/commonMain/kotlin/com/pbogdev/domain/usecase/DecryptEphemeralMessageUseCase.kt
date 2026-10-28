package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.EphemeralMessage
import com.pbogdev.domain.repository.EphemeralMessageRepository
import com.pbogdev.domain.usecase.base.BaseUseCase

class DecryptEphemeralMessageUseCase(private val ephemeralMessageRepository: EphemeralMessageRepository) :
    BaseUseCase<EphemeralMessage, DecryptEphemeralMessageUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<EphemeralMessage> =
        ephemeralMessageRepository.decryptMessage(
            encryptedMessage = params.encryptedMessage,
            senderBeaconId = params.targetBeaconId,
            expiresAtEpochMillis = params.expiresAtEpochMillis
        )

    data class Params(val encryptedMessage: String, val targetBeaconId: String, val expiresAtEpochMillis: Long)
}