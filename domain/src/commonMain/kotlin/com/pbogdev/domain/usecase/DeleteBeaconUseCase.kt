package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import com.pbogdev.domain.usecase.base.BaseUseCase

class DeleteBeaconUseCase(private val beaconRepository: BeaconRepository) :
    BaseUseCase<Unit, DeleteBeaconUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        beaconRepository.deleteMyBeacon(
            beaconId = params.beaconId
        )

    data class Params( val beaconId: String)
}