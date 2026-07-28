package com.pbogdev.domain

import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult

interface LocalBeaconManager {
    suspend fun saveMyBeacon(beacon: Beacon): CustomResult<Unit>
    suspend fun getMyBeacon(): CustomResult<Beacon>
    suspend fun clearMyBeacon(): CustomResult<Unit>  // Needed for the "Digital Shredder" / when TTL expires
}