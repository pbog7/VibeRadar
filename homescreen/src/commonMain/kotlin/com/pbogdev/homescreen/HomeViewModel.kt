package com.pbogdev.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbogdev.core.appLogger
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.GetExamplesUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.GetTextEmbeddingUseCase
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getExamplesUseCase: GetExamplesUseCase,
    private val getNearbyBeaconsUseCase: GetNearbyBeaconsUseCase,
    private val saveGeohashUseCase: SaveGeohashUseCase,
    private val uploadBeaconUseCase: UploadBeaconUseCase,
    private val getTextEmbeddingUseCase: GetTextEmbeddingUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState


    suspend fun uploadBeacon(
        expiresAt: Long,
    ): CustomResult<Unit> {
        val vibe = viewState.value.vibe.text.toString()
        val textEmbeddingResult =
            getTextEmbeddingUseCase(GetTextEmbeddingUseCase.Params(text = vibe))
        when (textEmbeddingResult) {
            is CustomResult.Success -> {
                val beacon = Beacon(
                    vibeVector = textEmbeddingResult.data,
                    expiresAt = expiresAt,
                    profile = viewState.value.profile,
                    vibe = vibe
                )
                _viewState.update { it.copy(myBeacon = beacon) }
                val uploadBeaconResult = uploadBeaconUseCase(UploadBeaconUseCase.Params(beacon))
                _viewState.update { it.copy(isLoading = false) }
                return uploadBeaconResult
            }

            is CustomResult.Failure -> {
                return textEmbeddingResult
            }
        }
    }

    init {
        viewModelScope.launch {

            when (val result = saveGeohashUseCase(
                SaveGeohashUseCase.Params(
                    latitude = 42.0,
                    longitude = 21.4,
                    precision = 5
                )
            )) {
                is CustomResult.Failure -> appLogger.i("SaveGeohash failed ${result.error}")
                is CustomResult.Success -> appLogger.i("Saved geohash success")
            }
            when (val getNearbyBeaconsResult = getNearbyBeaconsUseCase()) {
                is CustomResult.Success -> {
                    appLogger.i { getNearbyBeaconsResult.toString() }
                    _viewState.update { it.copy(nearbyBeacons = getNearbyBeaconsResult.data) }
                }

                is CustomResult.Failure -> appLogger.i("GetNearbyBeacons failed with error ${getNearbyBeaconsResult.error.message}")
            }

        }
    }

}