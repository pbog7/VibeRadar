package com.pbogdev.domain.usecase

import com.pbogdev.domain.LocationManager
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCase

class SaveGeohashUseCase(private val locationManager: LocationManager) :
    BaseUseCase<Unit, SaveGeohashUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        locationManager.saveGeohash(
            latitude = params.latitude,
            longitude = params.longitude,
            precision = params.precision
        )

    data class Params(val latitude: Double, val longitude: Double, val precision: Int = 5)
}