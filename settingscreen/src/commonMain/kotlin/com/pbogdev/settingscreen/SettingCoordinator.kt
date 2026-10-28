package com.pbogdev.settingscreen

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun SettingsScreen(
    onCloseSettings: () -> Unit // This closes the whole modal back to the radar
) {
    // Start on the Main Menu
    var currentDestination by rememberSaveable(stateSaver = SettingsDestinationSaver) {
        mutableStateOf(SettingsDestination.Menu)
    }

    // A smooth crossfade transition between settings screens
    Crossfade(targetState = currentDestination, label = "SettingsNav") { screen ->
        when (screen) {
            is SettingsDestination.Menu -> {
                SettingsMenuLayout(
                    onNavigateToPrivacy = {
                        currentDestination = SettingsDestination.PrivacyPolicy
                    },
                    onNavigateToTerms = { currentDestination = SettingsDestination.TermsOfService },
                    onClose = onCloseSettings // Exits completely
                )
            }

            is SettingsDestination.PrivacyPolicy -> {
                PrivacyPolicyScreen(
                    onNavigateBack = {
                        currentDestination = SettingsDestination.Menu
                    }
                )
            }

            is SettingsDestination.TermsOfService -> {
                TermsOfServiceScreen(
                    onNavigateBack = { currentDestination = SettingsDestination.Menu },
                    onAccept = {}
                )
            }
        }
    }
}