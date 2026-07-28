package com.pbogdev.settingscreen

import androidx.compose.runtime.saveable.Saver

val SettingsDestinationSaver = Saver<SettingsDestination, String>(
    save = { state ->
        // Convert the object to a primitive string for saving
        when (state) {
            SettingsDestination.Menu -> "Menu"
            SettingsDestination.PrivacyPolicy -> "PrivacyPolicy"
            SettingsDestination.TermsOfService -> "TermsOfService"
            SettingsDestination.DataConfiguration -> "DataConfiguration"
        }
    },
    restore = { savedString ->
        // Convert the primitive string back to your object
        when (savedString) {
            "PrivacyPolicy" -> SettingsDestination.PrivacyPolicy
            "TermsOfService" -> SettingsDestination.TermsOfService
            "DataConfiguration" -> SettingsDestination.DataConfiguration
            else -> SettingsDestination.Menu
        }
    }
)