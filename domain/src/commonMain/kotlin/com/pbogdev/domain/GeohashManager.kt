package com.pbogdev.domain

import com.pbogdev.domain.models.CustomResult

interface GeohashManager {

    suspend fun fetchCurrentGeohashGrid(): CustomResult<Set<String>>
    suspend fun fetchCurrentGeohash(): CustomResult<String>

    suspend fun saveGeohash(latitude: Double, longitude: Double, precision: Int = 5): CustomResult<Unit>

    suspend fun clearLocationData(): CustomResult<Unit>
}