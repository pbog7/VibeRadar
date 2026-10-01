package com.pbogdev.data.local.database

import androidx.room3.RoomDatabaseConstructor

@Suppress("KotlinNoActualForExpect")
expect object VibeDatabaseConstructor : RoomDatabaseConstructor<VibeDatabase> {
    override fun initialize(): VibeDatabase
}