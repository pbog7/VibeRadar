package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.EphemeralMessageRepository
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class SyncActiveConnectionsUseCase(private val ephemeralMessageRepository: EphemeralMessageRepository):
    BaseUseCaseNoParams<Unit> {
    override suspend fun invoke(): CustomResult<Unit> =
        ephemeralMessageRepository.syncActiveConnections()
}
