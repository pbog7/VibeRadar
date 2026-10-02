package com.pbogdev.data.local.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pbogdev.data.local.database.dao.ActiveConnectionsDao
import com.pbogdev.data.local.database.entities.ActiveConnectionEntity
import com.pbogdev.data.local.database.entities.BeaconEntity

@Database(entities = [ActiveConnectionEntity::class, BeaconEntity::class], version = 1)
@ConstructedBy(VibeDatabaseConstructor::class)
abstract class VibeDatabase : RoomDatabase() {
    abstract fun getActiveConnectionsDao(): ActiveConnectionsDao
}