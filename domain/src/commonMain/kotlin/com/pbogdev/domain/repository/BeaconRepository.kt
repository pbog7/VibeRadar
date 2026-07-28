package com.pbogdev.domain.repository

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult

interface BeaconRepository {
    suspend fun getNearbyBeacons(userBeacon: Beacon): CustomResult<List<MatchmakingBeacon>>

    suspend fun uploadBeacon(beacon: Beacon): CustomResult<Unit>
}