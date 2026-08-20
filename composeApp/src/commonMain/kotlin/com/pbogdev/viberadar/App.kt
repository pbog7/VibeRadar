package com.pbogdev.viberadar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.AnonymousSignInUseCase
import com.pbogdev.homescreen.HomeScreen
import com.pbogdev.settingscreen.SettingsCoordinator
import com.pbogdev.sharedui.components.systemActionLauncher.SystemActionLauncher
import com.pbogdev.sharedui.theme.VibeRadarTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    VibeRadarTheme {
        val anonymousSignInUseCase: AnonymousSignInUseCase = koinInject()
        var isAuthenticating by remember { mutableStateOf(true) }
        var authFailed by remember { mutableStateOf(false) }
        var isSettingsOpen by rememberSaveable { mutableStateOf(false) }
        val systemActionLauncher = koinInject<SystemActionLauncher>()

        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = true, // You can toggle this dynamically
            onBackCompleted = {
                if(isSettingsOpen){
                    isSettingsOpen = false
                }
                //back pressed logic here
            }
        )
        LaunchedEffect(Unit) {
            when (anonymousSignInUseCase()) {
                is CustomResult.Success -> {
                    isAuthenticating = false
                }

                is CustomResult.Failure -> {
                    isAuthenticating = false
                    authFailed = true
                }
            }
        }
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxSize(),
        ) {
            if (isAuthenticating) {
                // Show a loading spinner while signing in
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (authFailed) {
                // Show an error screen if sign in fails (e.g. no internet)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to connect to VibeRadar. Please check your internet.")
                }
            } else {
                // Once authenticated, show the main screen
                HomeScreen(openSettings = { isSettingsOpen = true }, openSystemSettings = systemActionLauncher::openAppSettings)
                AnimatedVisibility(
                    visible = isSettingsOpen,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // We load the Coordinator, which handles its own internal routing!
                    SettingsCoordinator(
                        onCloseSettings = { isSettingsOpen = false }
                    )
                }
            }
        }
    }
}