package com.pbogdev.domain.repository

import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult

interface BeaconRepository {
    suspend fun getNearbyBeacons(): CustomResult<List<Beacon>>
}