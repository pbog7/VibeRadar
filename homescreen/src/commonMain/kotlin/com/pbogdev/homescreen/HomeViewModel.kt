package com.pbogdev.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbogdev.core.appLogger
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.MatchmakingResult
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.Profile
import com.pbogdev.domain.usecase.GetCurrentLocationUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.GetTextEmbeddingUseCase
import com.pbogdev.domain.usecase.GetUserBeaconUseCase
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import com.pbogdev.domain.usecase.SetUserBeaconUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase.Params
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock.System
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    private val getNearbyBeaconsUseCase: GetNearbyBeaconsUseCase,
    private val saveGeohashUseCase: SaveGeohashUseCase,
    private val uploadBeaconUseCase: UploadBeaconUseCase,
    private val getTextEmbeddingUseCase: GetTextEmbeddingUseCase,
    private val getUserBeaconUseCase: GetUserBeaconUseCase,
    private val setUserBeaconUseCase: SetUserBeaconUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
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
        // temporary until delete functionality is added
        vibeState.clearText()

        _viewState.update { it.copy(myBeacon = null, uploadNewBeacon = true) }
    }

    private fun setBeacon() {
        viewModelScope.launch {
            viewState.value.myBeacon?.let {
                setUserBeaconUseCase(SetUserBeaconUseCase.Params(it))
            }
        }
    }

    private fun setDummyMatchmakingBeacons() {
        val dummyVector = FloatArray(512) { 0.1f }
        val futureTime = System.now().toEpochMilliseconds() + 86400000L // +1 day
        viewModelScope.launch {
            delay(5.seconds)
            _viewState.update {
                it.copy(
                    uploadNewBeacon = false,
                    radarState = RadarState.MATCHES,
                    nearbyBeacons = listOf(
                        // 1. The "Soulmate" (High scores across the board, full profile)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-1",
                                vibeVector = dummyVector,
                                vibe = "Looking for someone to grab artisan coffee and debate Clean Architecture patterns.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-1",
                                    likes = "Kotlin, Coffee, Dark Mode, Mechanical Keyboards",
                                    dislikes = "Spaghetti code, loud spaces",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.95f, 0.05f, 0.98f, 0.96f)
                        ),

                        // 2. The "Ghost" (No profile at all, purely matched on the ephemeral vibe)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-2",
                                vibeVector = dummyVector,
                                vibe = "Just visiting the city for the weekend, anyone want to show me the best local street food?",
                                expiresAt = futureTime,
                                profile = null // Variant: Null Profile
                            ),
                            matchResult = MatchmakingResult(0f, null, 0.88f, 0.85f)
                        ),

                        // 3. The "Wall of Text" (Testing maximum UI expansion and text wrapping)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-3",
                                vibeVector = dummyVector,
                                vibe = "I'm incredibly passionate about building decentralized systems and low-level cryptographic pipelines. Right now, I'm trying to figure out how to optimize a 512-dimensional vector search on device without blowing up the memory heap. If you understand what any of this means, please let's grab a beer and talk for 5 hours straight because I'm losing my mind staring at the IDE.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-3",
                                    likes = "Cryptography, P2P Networks, Deep Tech, Startups, IPAs",
                                    dislikes = "Centralized servers, proprietary software, early mornings",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.75f, 0.10f, 0.80f, 0.78f)
                        ),

                        // 4. The "Positivity Only" (Null dislikes, great match)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-4",
                                vibeVector = dummyVector,
                                vibe = "Skateboarding down by the pier today! Come say hi.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-4",
                                    likes = "Skating, Sunshine, Indie Rock",
                                    dislikes = null, // Variant: Null dislikes
                                    likesVector = dummyVector,
                                    dislikesVector = null
                                )
                            ),
                            matchResult = MatchmakingResult(0.90f, null, 0.85f, 0.88f)
                        ),

                        // 5. The "Empty Likes" (Profile exists, but likes is blank)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-5",
                                vibeVector = dummyVector,
                                vibe = "Anyone up for a quick 5k run in the park?",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-5",
                                    likes = "", // Variant: Empty string likes
                                    dislikes = "Shin splints, rain",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.10f, 0.05f, 0.90f, 0.65f)
                        ),

                        // 6. The "Terrible Match" (Low scores to test sorting/tiers)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-6",
                                vibeVector = dummyVector,
                                vibe = "Going to the loud, overcrowded club downtown.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-6",
                                    likes = "EDM, VIP sections, Bottle service",
                                    dislikes = "Quiet nights in, reading, cats",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(
                                0.15f,
                                0.90f,
                                0.20f,
                                0.18f
                            ) // Very low match
                        ),

                        // 7. Minimalist (Very short texts to ensure the UI card doesn't stretch awkwardly)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-7",
                                vibeVector = dummyVector,
                                vibe = "Gym time.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-7",
                                    likes = "Lifting",
                                    dislikes = "Rest days",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.60f, 0.40f, 0.65f, 0.62f)
                        ),

                        // 8. The "Dealbreaker" (High likes, but dislikes clashed massively)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-8",
                                vibeVector = dummyVector,
                                vibe = "Hanging at the dog cafe!",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-8",
                                    likes = "Dogs, Coffee, Parks",
                                    dislikes = "Cats, Video Games", // User loves cats and games
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(
                                0.85f,
                                0.95f,
                                0.80f,
                                0.45f
                            ) // Dislikes tanked the overall
                        ),

                        // 9. No profile, long vibe
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-9",
                                vibeVector = dummyVector,
                                vibe = "Does anyone know where the secret entrance to that underground speakeasy is? I've been walking in circles for 20 minutes and my GPS is glitching out.",
                                expiresAt = futureTime,
                                profile = null
                            ),
                            matchResult = MatchmakingResult(0f, null, 0.70f, 0.70f)
                        ),

                        // 10. Weird characters and emojis (Testing UI rendering)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-10",
                                vibeVector = dummyVector,
                                vibe = "🎮👾 Playing some retro SNES games! 🍕🍻",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-10",
                                    likes = "Retro gaming, Pizza, 90s nostalgia",
                                    dislikes = "Lag, pay-to-win games",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.92f, 0.12f, 0.95f, 0.93f)
                        ),

                        // 11. The Hater (Blank likes, only dislikes)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-11",
                                vibeVector = dummyVector,
                                vibe = "I'm just here to complain about the weather.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-11",
                                    likes = "",
                                    dislikes = "Rain, cold, humidity, traffic, people walking slow",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.0f, 0.20f, 0.40f, 0.35f)
                        ),

                        // 12. Extremely average match
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-12",
                                vibeVector = dummyVector,
                                vibe = "Doing some grocery shopping.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-12",
                                    likes = "Cooking, meal prep",
                                    dislikes = null,
                                    likesVector = dummyVector,
                                    dislikesVector = null
                                )
                            ),
                            matchResult = MatchmakingResult(0.50f, null, 0.50f, 0.50f)
                        ),

                        // 13. High vibe match, low profile match
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-13",
                                vibeVector = dummyVector,
                                vibe = "Writing Kotlin code at the local library.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-13",
                                    likes = "Sports, extreme hiking, skydiving", // Different long-term interests
                                    dislikes = "Sitting still",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(
                                0.20f,
                                0.30f,
                                0.99f,
                                0.72f
                            ) // Vibe carried the score
                        ),

                        // 14. Huge comma-separated likes list
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-14",
                                vibeVector = dummyVector,
                                vibe = "Relaxing.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-14",
                                    likes = "Yoga, Meditation, Tea, Reading, Journaling, Incense, Ambient Music, Stretching, Plants, Candles, Deep breathing",
                                    dislikes = "Chaos",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.88f, 0.0f, 0.40f, 0.65f)
                        ),

                        // 15. The Perfect 100
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-15",
                                vibeVector = dummyVector,
                                vibe = "Building an open-source privacy-first KMP app.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-15",
                                    likes = "Kotlin, KMP, Privacy, Open Source, Jetpack Compose",
                                    dislikes = "Tracking, Data Brokers",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(1.0f, 0.0f, 1.0f, 1.0f)
                        ),

                        // 16. The Almost Zero
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-16",
                                vibeVector = dummyVector,
                                vibe = "Selling my data to brokers for $5.",
                                expiresAt = futureTime,
                                profile = null
                            ),
                            matchResult = MatchmakingResult(0.0f, null, 0.01f, 0.01f)
                        ),

                        // 17. Null dislikes, missing likes text
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-17",
                                vibeVector = dummyVector,
                                vibe = "Walking my dog.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-17",
                                    likes = "",
                                    dislikes = null,
                                    likesVector = dummyVector,
                                    dislikesVector = null
                                )
                            ),
                            matchResult = MatchmakingResult(0f, null, 0.60f, 0.55f)
                        ),

                        // 18. Dislikes match only (Bonding over shared hatred)
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-18",
                                vibeVector = dummyVector,
                                vibe = "I hate this traffic.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-18",
                                    likes = "Nothing",
                                    dislikes = "Traffic, lines, waiting",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(
                                0.1f,
                                0.0f,
                                0.70f,
                                0.65f
                            ) // DislikesMatchScore is 0.0 (no conflict), but algorithmically you'd likely adjust this based on your gamma weight
                        ),

                        // 19. Super short vibe, long dislikes
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-19",
                                vibeVector = dummyVector,
                                vibe = "Bored.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-19",
                                    likes = "Movies",
                                    dislikes = "Horror movies, jump scares, gore, psychological thrillers, found footage films",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.50f, 0.10f, 0.30f, 0.40f)
                        ),

                        // 20. The completely average anonymous ping
                        MatchmakingBeacon(
                            beacon = Beacon(
                                beaconId = "beacon-20",
                                vibeVector = dummyVector,
                                vibe = "Reading a book at the corner cafe.",
                                expiresAt = futureTime,
                                profile = Profile(
                                    id = "prof-20",
                                    likes = "Books, Coffee",
                                    dislikes = "Loud noises",
                                    likesVector = dummyVector,
                                    dislikesVector = dummyVector
                                )
                            ),
                            matchResult = MatchmakingResult(0.65f, 0.05f, 0.85f, 0.75f)
                        )
                    ).sortedByDescending { matchItem -> matchItem.matchResult.overallMatchScore })
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

    init {
        viewModelScope.launch {
            when (val getUserBeaconResult = getUserBeaconUseCase()) {
                is CustomResult.Success -> {
                    _viewState.update {
                        it.copy(
                            myBeacon = getUserBeaconResult.data,
                            uploadNewBeacon = false
                        )
                    }
                }

                is CustomResult.Failure -> {
                    _viewState.update { it.copy(uploadNewBeacon = true) }
                }
            }
        }
    }

}