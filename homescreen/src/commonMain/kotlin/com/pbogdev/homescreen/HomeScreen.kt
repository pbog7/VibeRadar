package com.pbogdev.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pbogdev.aimatchmakingengine.VibeTextEmbedder
import com.pbogdev.core.appLogger
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.viewState.collectAsState()
    appLogger.i { "Home Screen composition" }
    val vibeTextEmbedder: VibeTextEmbedder = koinInject()
    LaunchedEffect(Unit){
        vibeTextEmbedder.initialize()
    }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VibeInput(state = state.vibe)
        SendVibeBtn({
            appLogger.i { "Vibe ${state.vibe.text} sent" }
            scope.launch {
                val result = vibeTextEmbedder.embed(state.vibe.text.toString())
                result?.forEach { appLogger.i { "$it" } }
            }
        }

        )
    }

}

@Composable
fun VibeInput(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    TextField(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
    )
}

@Composable
fun SendVibeBtn(onClick:()-> Unit) {
    Button(onClick = { onClick() }) {
        Text("Send Vibe")
    }
}
