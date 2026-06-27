package com.pbogdev.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.pbogdev.data.local.PREFERENCES_DATASTORE_FILE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformDatastoreModule: Module = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                // 1. Ask Apple's Foundation API for the secure Documents folder
                val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )

                // 2. Append our exact filename to the path
                val path = requireNotNull(documentDirectory).path + PREFERENCES_DATASTORE_FILE_NAME

                // 3. Convert it to a Multiplatform Okio Path
                path.toPath()
            }
        )
    }
}