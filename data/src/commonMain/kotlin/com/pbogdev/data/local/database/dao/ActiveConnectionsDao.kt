package com.pbogdev.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import com.pbogdev.data.local.database.entities.ActiveConnectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveConnectionsDao {
    @Upsert
    suspend fun upsertActiveConnections(activeConnections: List<ActiveConnectionEntity>)

    @Upsert
    suspend fun upsertActiveConnection(activeConnection:ActiveConnectionEntity)

    @Query("SELECT * FROM active_connections")
    fun observeActiveConnections(): Flow<List<ActiveConnectionEntity>>

    @Query("SELECT * FROM active_connections")
    suspend fun getActiveConnectionsSnapshot():List<ActiveConnectionEntity>

    @Query("SELECT * FROM active_connections WHERE beaconId = :beaconId")
    suspend fun getActiveConnectionByBeaconId(beaconId: String): ActiveConnectionEntity?

    @Update
    suspend fun updateActiveConnection(entity: ActiveConnectionEntity)

    @Query("DELETE FROM active_connections")
    suspend fun deleteAllActiveConnections()
}