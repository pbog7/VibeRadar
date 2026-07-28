package com.pbogdev.domain.usecase

import com.pbogdev.domain.LocalBeaconManager
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class GetUserBeaconUseCase(private val localBeaconManager: LocalBeaconManager):
    BaseUseCaseNoParams<Beacon> {
    override suspend fun invoke(): CustomResult<Beacon> =
        localBeaconManager.getMyBeacon()
}