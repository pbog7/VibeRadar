package com.pbogdev.domain.usecase

import com.pbogdev.domain.matchmaking.VibeTextEmbedder
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.EphemeralMessageRepository
import com.pbogdev.domain.usecase.base.BaseUseCase

class DeleteEphemeralMessageUseCase(private val ephemeralMessageRepository: EphemeralMessageRepository) :
    BaseUseCase<Unit, DeleteEphemeralMessageUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        ephemeralMessageRepository.deleteMessage(
            messageId = params.messageId,
            beaconId = params.beaconId
        )

    data class Params(val messageId: String, val beaconId: String)
}