package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import com.pbogdev.domain.usecase.base.BaseUseCase

class UploadBeaconUseCase(private val beaconRepository: BeaconRepository) :
    BaseUseCase<Unit, UploadBeaconUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        beaconRepository.uploadBeacon(
            beacon = params.beacon
        )


    data class Params(val beacon: Beacon)
}

