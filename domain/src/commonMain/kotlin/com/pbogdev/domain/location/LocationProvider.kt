package com.pbogdev.domain.location

import com.pbogdev.domain.models.CustomResult

interface LocationProvider {
    /**
     * Fetches the current coarse location of the device.
     */
    suspend fun getCurrentLocation(): CustomResult<LocationCoordinates>
}