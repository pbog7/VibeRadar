package com.pbogdev.sharedui

import com.pbogdev.sharedui.components.systemActionLauncher.SystemActionLauncher


import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class IOSSystemActionLauncher : SystemActionLauncher {

    override fun openAppSettings() {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}