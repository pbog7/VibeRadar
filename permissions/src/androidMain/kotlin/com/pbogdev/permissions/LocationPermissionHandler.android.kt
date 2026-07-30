package com.pbogdev.permissions

import androidx.compose.runtime.Composable


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberLocationPermissionHandler(
    onResult: (Boolean) -> Unit
): LocationPermissionHandler {
    val context = LocalContext.current

    // Check initial OS state to see if we already have it
    var isGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Google's official Compose API for triggering Permission intents
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        onResult(granted)
    }

    return remember(launcher, isGranted) {
        object : LocationPermissionHandler {
            override val isGranted: Boolean
                get() = isGranted

            override fun requestPermission() {
                if (!isGranted) {
                    // Fire the exact, stripped-down coarse intent
                    launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                } else {
                    onResult(true) // Already granted, just fire success
                }
            }
        }
    }
}