package com.pbogdev.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "beacons")
data class BeaconEntity(
    @PrimaryKey val beaconId: String,
    val payload: String
)