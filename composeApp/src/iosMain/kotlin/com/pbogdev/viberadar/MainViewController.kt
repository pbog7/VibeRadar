package com.pbogdev.viberadar

import androidx.compose.ui.window.ComposeUIViewController
import com.pbogdev.viberadar.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}