package com.pbogdev.domain.usecase

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import com.pbogdev.domain.usecase.base.BaseUseCase
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class GetNearbyBeaconsUseCase(private val beaconRepository: BeaconRepository) :
    BaseUseCase<List<MatchmakingBeacon>, GetNearbyBeaconsUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<List<MatchmakingBeacon>> =
        beaconRepository.getNearbyBeacons(
            userBeacon = params.userBeacon
        )


    data class Params(val userBeacon: Beacon)
}