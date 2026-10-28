package com.pbogdev.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbogdev.core.appLogger
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.Profile
import com.pbogdev.domain.usecase.DeleteBeaconUseCase
import com.pbogdev.domain.usecase.DeleteLocalBeaconUseCase
import com.pbogdev.domain.usecase.SyncActiveConnectionsUseCase
import com.pbogdev.domain.usecase.GetCurrentLocationUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.GetTextEmbeddingUseCase
import com.pbogdev.domain.usecase.GetUserBeaconUseCase
import com.pbogdev.domain.usecase.ObserveActiveConnectionsUseCase
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import com.pbogdev.domain.usecase.SetUserBeaconUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase.Params
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock.System

class HomeViewModel(
    private val getNearbyBeaconsUseCase: GetNearbyBeaconsUseCase,
    private val saveGeohashUseCase: SaveGeohashUseCase,
    private val uploadBeaconUseCase: UploadBeaconUseCase,
    private val getTextEmbeddingUseCase: GetTextEmbeddingUseCase,
    private val getUserBeaconUseCase: GetUserBeaconUseCase,
    private val setUserBeaconUseCase: SetUserBeaconUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val syncActiveConnectionsUseCase: SyncActiveConnectionsUseCase,
    private val observeActiveConnectionsUseCase: ObserveActiveConnectionsUseCase,
    private val deleteBeaconUseCase: DeleteBeaconUseCase,
    private val deleteLocalBeaconUseCase: DeleteLocalBeaconUseCase
) : ViewModel() {

    val vibeState = TextFieldState()
    val likesState = TextFieldState()
    val dislikesState = TextFieldState()
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    fun setRadarState(radarState: RadarState) {
        _viewState.update { it.copy(radarState = radarState) }
    }

    suspend fun updateLocation(): CustomResult<Unit> {
        return when (val getCurrentLocationResult = getCurrentLocationUseCase()) {
            is CustomResult.Success -> {
                when (val saveGeohashResult = saveGeohashUseCase(
                    SaveGeohashUseCase.Params(
                        latitude = getCurrentLocationResult.data.latitude,
                        longitude = getCurrentLocationResult.data.longitude
                    )
                )) {
                    is CustomResult.Failure -> {
                        saveGeohashResult
                    }

                    is CustomResult.Success -> {
                        appLogger.i { "SaveGeohash finished ${getCurrentLocationResult.data} $saveGeohashResult" }
                        CustomResult.Success(Unit)
                    }

                }
            }

            is CustomResult.Failure -> {
                appLogger.i { "GetCurrentLocation failed ${getCurrentLocationResult.error}" }
                getCurrentLocationResult
            }

        }
    }

    suspend fun findMatch(): CustomResult<Unit> {
        _viewState.update { it.copy(radarState = RadarState.SEARCHING) }
        updateLocation()
        if (viewState.value.uploadNewBeacon) {
            val uploadResult = uploadBeacon()
            if (uploadResult is CustomResult.Failure) {
                return uploadResult
            }
        }
        val nearbyResult = getNearbyBeacons()
        if (nearbyResult is CustomResult.Failure) {
            return nearbyResult
        }
//        setDummyMatchmakingBeacons()
        return CustomResult.Success(Unit)
    }

    private suspend fun uploadBeacon(): CustomResult<Unit> {
        val vibe = vibeState.text.toString()
        val textEmbeddingResult =
            getTextEmbeddingUseCase(GetTextEmbeddingUseCase.Params(text = vibe))
        when (textEmbeddingResult) {
            is CustomResult.Success -> {
                updateProfile()
                val beacon = Beacon(
                    vibeVector = textEmbeddingResult.data,
                    expiresAt = System.now().toEpochMilliseconds() + 86400000L,
                    profile = viewState.value.profile,
                    vibe = vibe
                )
                _viewState.update { it.copy(myBeacon = beacon) }
                val uploadBeaconResult = uploadBeaconUseCase(Params(beacon))
                when (uploadBeaconResult) {
                    is CustomResult.Success -> {
                        _viewState.update { it.copy(uploadNewBeacon = false) }
                        setBeacon()
                    }

                    is CustomResult.Failure -> {
                        _viewState.update { it.copy(radarState = RadarState.IDLE) }
                    }
                }
                return uploadBeaconResult
            }

            is CustomResult.Failure -> {
                _viewState.update { it.copy(radarState = RadarState.IDLE) }
                return textEmbeddingResult
            }
        }
    }

    private suspend fun getNearbyBeacons(): CustomResult<Unit> {
        val myBeacon =
            viewState.value.myBeacon ?: return CustomResult.Failure(CustomError.BeaconNotStored())
        when (val getNearbyBeaconsResult =
            getNearbyBeaconsUseCase(GetNearbyBeaconsUseCase.Params(myBeacon))) {
            is CustomResult.Success -> {
                appLogger.i { getNearbyBeaconsResult.toString() }
                _viewState.update {
                    it.copy(
                        nearbyBeacons = getNearbyBeaconsResult.data,
                        radarState = RadarState.MATCHES
                    )
                }
                return CustomResult.Success(Unit)
            }

            is CustomResult.Failure -> {
                _viewState.update {
                    it.copy(radarState = RadarState.IDLE)
                }
                appLogger.i("GetNearbyBeacons failed with error ${getNearbyBeaconsResult.error.message}")
                return getNearbyBeaconsResult
            }
        }
    }

    fun deleteBeacon() {
        vibeState.clearText()
        viewModelScope.launch {
            deleteLocalBeaconUseCase()
        }
        viewState.value.myBeacon?.let {
            viewModelScope.launch {
                deleteBeaconUseCase(DeleteBeaconUseCase.Params(it.beaconId))
            }
        }
        _viewState.update { it.copy(myBeacon = null, uploadNewBeacon = true) }
    }

    private fun setBeacon() {
        viewModelScope.launch {
            viewState.value.myBeacon?.let {
                setUserBeaconUseCase(SetUserBeaconUseCase.Params(it))
            }
        }
    }

    private suspend fun updateProfile() {
        val currentProfile = viewState.value.profile
        val newLikes = likesState.text.toString().trim()
        val newDislikes = dislikesState.text.toString().trim()

        if (newLikes == (currentProfile?.likes ?: "") &&
            newDislikes == (currentProfile?.dislikes ?: "")
        ) {
            return
        }

        if (newLikes.isEmpty()) {
            if (currentProfile != null) {
                _viewState.update { it.copy(profile = null) }
            }
            return
        }

        val likesVec: FloatArray = if (newLikes != currentProfile?.likes) {
            val result = getTextEmbeddingUseCase(GetTextEmbeddingUseCase.Params(newLikes))
            if (result is CustomResult.Success) {
                result.data
            } else {
                return
            }
        } else {
            currentProfile.likesVector
        }

        // 4. OPTIONAL DISLIKES EMBEDDING (Nullable)
        val dislikesVec: FloatArray? = when {
            newDislikes.isEmpty() -> null
            newDislikes != currentProfile?.dislikes ->
                (getTextEmbeddingUseCase(GetTextEmbeddingUseCase.Params(newDislikes)) as? CustomResult.Success)?.data

            else -> currentProfile.dislikesVector
        }

        val updatedProfile = currentProfile?.copy(
            likes = newLikes,
            likesVector = likesVec,
            dislikes = newDislikes,
            dislikesVector = dislikesVec
        ) ?: Profile(
            likes = newLikes,
            likesVector = likesVec,
            dislikes = newDislikes,
            dislikesVector = dislikesVec
        )

        _viewState.update { it.copy(profile = updatedProfile) }
    }

    private fun observeActiveConnections() {
        viewModelScope.launch {
            observeActiveConnectionsUseCase().collect { activeConnections ->
                _viewState.update {
                    it.copy(activeConnectionsList = activeConnections)
                }
            }
        }
    }

    init {
        observeActiveConnections()
        viewModelScope.launch {
            when (val getUserBeaconResult = getUserBeaconUseCase()) {
                is CustomResult.Success -> {
                    _viewState.update {
                        it.copy(
                            myBeacon = getUserBeaconResult.data,
                            uploadNewBeacon = false
                        )
                    }
                    vibeState.setTextAndPlaceCursorAtEnd(getUserBeaconResult.data.vibe)
                    getUserBeaconResult.data.profile?.let { profile ->
                        likesState.setTextAndPlaceCursorAtEnd(profile.likes)
                        profile.dislikes?.let { dislikes ->
                            dislikesState.setTextAndPlaceCursorAtEnd(dislikes)
                        }
                    }
                    when (val getActiveConnectionsUseCaseResult = syncActiveConnectionsUseCase()) {
                        is CustomResult.Success -> {
                            appLogger.d { "GetActiveConnections success " }
                        }

                        is CustomResult.Failure -> {
                            appLogger.d { "GetActiveConnections failed with error ${getActiveConnectionsUseCaseResult.error}" }
                        }
                    }
                }

                is CustomResult.Failure -> {
                    _viewState.update { it.copy(uploadNewBeacon = true) }
                }
            }
        }
    }

}