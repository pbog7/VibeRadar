package com.pbogdev.sharedui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.pbogdev.sharedui.components.systemActionLauncher.SystemActionLauncher

class AndroidSystemActionLauncher(
    private val context: Context
) : SystemActionLauncher {

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}