package com.pbogdev.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.ConnectionStatus

@Entity(tableName = "active_connections")
data class ActiveConnectionEntity(
    @PrimaryKey val beaconId: String,
    val senderUid: String,
    val encryptedMessagePayload: String?,
    val matchmakingBeaconDtoString: String,
    val connectionStatus: String
)