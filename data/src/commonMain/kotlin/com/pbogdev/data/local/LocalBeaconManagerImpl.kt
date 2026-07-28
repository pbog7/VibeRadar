package com.pbogdev.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.data.local.VibeRadarPreferences.MY_BEACON
import com.pbogdev.data.utils.appJson
import com.pbogdev.data.utils.safeDecodeFromString
import com.pbogdev.data.utils.safeEncodeToString
import com.pbogdev.domain.LocalBeaconManager
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlinx.coroutines.flow.firstOrNull

class LocalBeaconManagerImpl(
    private val datastore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : LocalBeaconManager {


    override suspend fun saveMyBeacon(beacon: Beacon): CustomResult<Unit> = safeResult {
        val beaconString = appJson.safeEncodeToString(beacon, dispatcherProvider)
        datastore.edit { preferences ->
            preferences[MY_BEACON] = beaconString
        }
        CustomResult.Success(Unit)
    }


    override suspend fun getMyBeacon(): CustomResult<Beacon> = safeResult {
        val beaconString = datastore.data.firstOrNull()?.get(MY_BEACON)
        if (beaconString != null) {
            CustomResult.Success(
                appJson.safeDecodeFromString(
                    beaconString,
                    dispatcherProvider
                )
            )
        } else {
            CustomResult.Failure(CustomError.BeaconNotStored())
        }
    }

    override suspend fun clearMyBeacon(): CustomResult<Unit> = safeResult {
        datastore.edit { preferences ->
            preferences.remove(MY_BEACON)
        }
        CustomResult.Success(Unit)
    }
}