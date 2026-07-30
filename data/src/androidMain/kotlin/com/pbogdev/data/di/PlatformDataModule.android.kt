package com.pbogdev.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pbogdev.data.local.PREFERENCES_DATASTORE_FILE_NAME
import com.pbogdev.data.location.AndroidLocationProvider
import com.pbogdev.data.network.auth.AndroidAnonymousAuthenticator
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.location.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_DATASTORE_FILE_NAME)

actual val platformDataModule: Module = module {
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }
    singleOf(::AndroidAnonymousAuthenticator) bind AnonymousAuthenticator::class
    singleOf(::AndroidLocationProvider) bind LocationProvider::class
}