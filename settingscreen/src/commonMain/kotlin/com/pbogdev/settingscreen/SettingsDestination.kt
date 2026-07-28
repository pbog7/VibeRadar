package com.pbogdev.settingscreen

sealed interface SettingsDestination {
    object Menu : SettingsDestination
    object PrivacyPolicy : SettingsDestination
    object TermsOfService : SettingsDestination
    object DataConfiguration : SettingsDestination
}