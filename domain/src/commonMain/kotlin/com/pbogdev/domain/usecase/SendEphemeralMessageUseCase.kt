package com.pbogdev.domain.usecase

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.EphemeralMessage
import com.pbogdev.domain.repository.EphemeralMessageRepository
import com.pbogdev.domain.usecase.base.BaseUseCase

class SendEphemeralMessageUseCase(private val ephemeralMessageRepository: EphemeralMessageRepository) :
    BaseUseCase<Unit, SendEphemeralMessageUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        ephemeralMessageRepository.sendMessage(
            ephemeralMessage = params.ephemeralMessage,
            matchmakingBeacon = params.matchmakingBeacon,
        )


    data class Params(val ephemeralMessage: EphemeralMessage, val matchmakingBeacon: MatchmakingBeacon)
}