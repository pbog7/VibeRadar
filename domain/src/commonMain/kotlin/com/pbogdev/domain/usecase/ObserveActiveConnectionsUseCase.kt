package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.repository.EphemeralMessageRepository
import com.pbogdev.domain.usecase.base.ObserveBaseUseCaseNoParams
import kotlinx.coroutines.flow.Flow

class ObserveActiveConnectionsUseCase(private val ephemeralMessageRepository: EphemeralMessageRepository) :
    ObserveBaseUseCaseNoParams<List<ActiveConnection>> {

    override fun invoke(): Flow<List<ActiveConnection>> =
        ephemeralMessageRepository.observeActiveConnections()
}