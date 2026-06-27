package com.pbogdev.data.local

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal const val PREFERENCES_DATASTORE_FILE_NAME = "vibeRadarPreferences_pb"

object VibeRadarPreferences {
    val LAST_KNOWN_GEOHASH = stringPreferencesKey("last_known_geohash")
    val LAST_KNOWN_GEOHASH_GRID = stringSetPreferencesKey("last_known_geohash_grid")
}
