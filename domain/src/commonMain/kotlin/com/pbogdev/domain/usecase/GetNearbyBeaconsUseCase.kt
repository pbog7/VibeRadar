package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository

class GetNearbyBeaconsUseCase(private val beaconRepository: BeaconRepository) :
    BaseUseCaseNoParams<List<Beacon>> {
    override suspend fun invoke(): CustomResult<List<Beacon>> =
        beaconRepository.getNearbyBeacons()
}