package com.pbogdev.domain.usecase

import com.pbogdev.domain.LocalBeaconManager
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCase

class SetUserBeaconUseCase(private val localBeaconManager: LocalBeaconManager) :
    BaseUseCase<Unit, SetUserBeaconUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        localBeaconManager.saveMyBeacon(
            beacon = params.userBeacon
        )


    data class Params(val userBeacon: Beacon)
}