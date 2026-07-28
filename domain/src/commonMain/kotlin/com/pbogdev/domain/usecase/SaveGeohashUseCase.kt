package com.pbogdev.domain.usecase

import com.pbogdev.domain.GeohashManager
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCase

class SaveGeohashUseCase(private val geohashManager: GeohashManager) :
    BaseUseCase<Unit, SaveGeohashUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<Unit> =
        geohashManager.saveGeohash(
            latitude = params.latitude,
            longitude = params.longitude,
            precision = params.precision
        )

    data class Params(val latitude: Double, val longitude: Double, val precision: Int = 5)
}