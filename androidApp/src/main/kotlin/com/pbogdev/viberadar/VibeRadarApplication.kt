package com.pbogdev.viberadar

import android.app.Application
import com.pbogdev.viberadar.di.initKoin
import org.koin.android.ext.koin.androidContext

class VibeRadarApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@VibeRadarApplication)
        }
    }
}