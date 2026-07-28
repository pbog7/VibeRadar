package com.pbogdev.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.pbogdev.data.local.VibeRadarPreferences.LAST_KNOWN_GEOHASH
import com.pbogdev.data.local.VibeRadarPreferences.LAST_KNOWN_GEOHASH_GRID
import com.pbogdev.data.location.GeohashEngine
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.GeohashManager
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlinx.coroutines.flow.firstOrNull

class GeohashManagerImpl(
    private val datastore: DataStore<Preferences>,
    private val geohashEngine: GeohashEngine
) : GeohashManager {
    override suspend fun fetchCurrentGeohashGrid(): CustomResult<Set<String>> = safeResult {
        val geohashGrid = datastore.data.firstOrNull()?.get(LAST_KNOWN_GEOHASH_GRID)
        if (!geohashGrid.isNullOrEmpty()) {
            CustomResult.Success(geohashGrid)
        } else {
            CustomResult.Failure(CustomError.GeohashNotStored())
        }
    }

    override suspend fun fetchCurrentGeohash(): CustomResult<String> = safeResult {
        val geohash = datastore.data.firstOrNull()?.get(LAST_KNOWN_GEOHASH)
        if (geohash != null) {
            CustomResult.Success(geohash)
        } else {
            CustomResult.Failure(CustomError.GeohashNotStored())
        }
    }

    override suspend fun saveGeohash(
        latitude: Double,
        longitude: Double,
        precision: Int
    ): CustomResult<Unit> = safeResult {
        val geohash: String
        val geohashGrid:Set<String>
        geohashEngine.apply {
            geohash = encode(latitude = latitude, longitude = longitude, precision = precision)
            geohashGrid = getNineBoxGrid(geohash).toSet()
        }
        datastore.edit { preferences ->
            preferences[LAST_KNOWN_GEOHASH] = geohash
            preferences[LAST_KNOWN_GEOHASH_GRID] = geohashGrid
        }
        CustomResult.Success(Unit)
    }

    override suspend fun clearLocationData(): CustomResult<Unit> = safeResult {
        datastore.edit { preferences ->
            preferences.remove(LAST_KNOWN_GEOHASH)
            preferences.remove(LAST_KNOWN_GEOHASH_GRID)
        }
        CustomResult.Success(Unit)
    }
}