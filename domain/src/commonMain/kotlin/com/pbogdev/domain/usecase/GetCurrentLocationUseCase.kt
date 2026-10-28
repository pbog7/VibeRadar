package com.pbogdev.domain.usecase

import com.pbogdev.domain.location.LocationCoordinates
import com.pbogdev.domain.location.LocationProvider
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class GetCurrentLocationUseCase(private val locationProvider: LocationProvider):
    BaseUseCaseNoParams<LocationCoordinates> {
    override suspend fun invoke(): CustomResult<LocationCoordinates> =
        locationProvider.getCurrentLocation()
}