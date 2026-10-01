package com.pbogdev.domain.usecase

import com.pbogdev.domain.LocalBeaconManager
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class DeleteLocalBeaconUseCase(private val localBeaconManager: LocalBeaconManager) :
    BaseUseCaseNoParams<Unit> {

    override suspend fun invoke(): CustomResult<Unit> =
        localBeaconManager.clearMyBeacon()


}