package com.pbogdev.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.pbogdev.core.appLogger
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.GetExamplesUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.Params
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getExamplesUseCase: GetExamplesUseCase,
    private val getNearbyBeaconsUseCase: GetNearbyBeaconsUseCase,
    private val saveGeohashUseCase: SaveGeohashUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        viewModelScope.launch {

            when(val result = saveGeohashUseCase(Params(latitude = 42.0, longitude = 21.4, precision = 5))){
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