package com.pbogdev.viberadar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.AnonymousSignInUseCase
import com.pbogdev.homescreen.HomeScreen
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    MaterialTheme {
        val anonymousSignInUseCase: AnonymousSignInUseCase = koinInject()
        var isAuthenticating by remember { mutableStateOf(true) }
        var authFailed by remember { mutableStateOf(false) }
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
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                HomeScreen()
            }
        }
    }
}