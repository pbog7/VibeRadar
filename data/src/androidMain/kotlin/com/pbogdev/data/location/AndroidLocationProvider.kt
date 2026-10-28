package com.pbogdev.data.location

import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pbogdev.domain.location.LocationCoordinates
import com.pbogdev.domain.location.LocationProvider
import kotlinx.coroutines.tasks.await

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // Suppressed because the UI layer handles the permission check
    override suspend fun getCurrentLocation(): CustomResult<LocationCoordinates> = safeResult {
        // Request balanced power accuracy (block level), perfectly matching our Coarse requirement
        val location = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).await()

        if (location != null) {
            CustomResult.Success(LocationCoordinates(location.latitude, location.longitude))
        } else {
            CustomResult.Failure(CustomError.LocationUnavailable())
        }
    }
}
