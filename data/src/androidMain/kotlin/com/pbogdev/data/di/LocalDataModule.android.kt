package com.pbogdev.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pbogdev.data.local.PREFERENCES_DATASTORE_FILE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_DATASTORE_FILE_NAME)

actual val platformDatastoreModule: Module = module {
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }
}