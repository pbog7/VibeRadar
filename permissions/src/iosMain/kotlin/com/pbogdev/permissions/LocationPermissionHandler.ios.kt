package com.pbogdev.permissions

import androidx.compose.runtime.Composable

import androidx.compose.runtime.*
import platform.CoreLocation.*
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionHandler(
    onResult: (Boolean) -> Unit
): LocationPermissionHandler {

    // Remember the manager so it survives recompositions
    val locationManager = remember { CLLocationManager() }

    // Check the current OS authorization status
    var isGranted by remember {
        mutableStateOf(
            locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                    locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedAlways
        )
    }

    // A native delegate to intercept the iOS dialog response
    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                val granted =
                    manager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                            manager.authorizationStatus == kCLAuthorizationStatusAuthorizedAlways
                isGranted = granted
                onResult(granted)
            }
        }
    }

    // Attach the delegate to the active Composable lifecycle
    DisposableEffect(locationManager) {
        locationManager.delegate = delegate
        onDispose {
            locationManager.delegate = null
        }
    }

    return remember(isGranted) {
        object : LocationPermissionHandler {
            override val isGranted: Boolean
                get() = isGranted

            override fun requestPermission() {
                if (!isGranted) {
                    // Failsafe hardware baseline: Force reduced accuracy
                    locationManager.desiredAccuracy = kCLLocationAccuracyReduced
                    locationManager.requestWhenInUseAuthorization()
                } else {
                    onResult(true)
                }
            }
        }
    }
}