package com.pbogdev.permissions

import androidx.compose.runtime.Composable

interface LocationPermissionHandler {
    val isGranted: Boolean
    fun requestPermission()
}

@Composable
expect fun rememberLocationPermissionHandler(
    onResult: (Boolean) -> Unit
): LocationPermissionHandler