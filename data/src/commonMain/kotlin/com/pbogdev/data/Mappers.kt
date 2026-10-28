package com.pbogdev.data

import com.pbogdev.data.local.database.entities.ActiveConnectionEntity
import com.pbogdev.data.network.dto.BeaconDto
import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.dto.MatchmakingBeaconDTO
import com.pbogdev.data.network.dto.MatchmakingResultDTO
import com.pbogdev.data.network.dto.ProfileDto
import com.pbogdev.data.utils.appJson
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.MatchmakingResult
import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.ConnectionStatus
import com.pbogdev.domain.models.ExampleModel
import com.pbogdev.domain.models.Profile
import io.ktor.util.valuesOf


fun ExampleDto.toExampleModel() = ExampleModel(
    example = example
)

fun ProfileDto.toProfile() = Profile(
    id = id,
    likes = likes,
    dislikes = dislikes,
    likesVector = likesVector,
    dislikesVector = dislikesVector
)

fun Profile.toProfileDto() = ProfileDto(
    id = id,
    likes = likes,
    dislikes = dislikes,
    likesVector = likesVector,
    dislikesVector = dislikesVector
)

fun BeaconDto.toBeacon() = Beacon(
    beaconId = beaconId,
    profile = profile?.toProfile(),
    vibeVector = vibeVector,
    expiresAt = expiresAtEpochMillis,
    vibe = vibe,
    senderUid = senderUid
)


fun Beacon.toBeaconDto(senderId: String) = BeaconDto(
    beaconId = beaconId,
    profile = profile?.toProfileDto(),
    vibeVector = vibeVector,
    expiresAtEpochMillis = expiresAt,
    vibe = vibe,
    senderUid = senderId
)

fun MatchmakingBeacon.toMatchmakingBeaconDTO(senderId: String) = MatchmakingBeaconDTO(
    beacon = beacon.toBeaconDto(senderId),
    matchResult = matchResult.toMatchmakingResultDTO()
)

fun MatchmakingBeaconDTO.toMatchmakingBeacon() = MatchmakingBeacon(
    beacon = beacon.toBeacon(),
    matchResult = matchResult.toMatchmakingResult()
)

fun MatchmakingResult.toMatchmakingResultDTO() = MatchmakingResultDTO(
    overallMatchScore = overallMatchScore,
    likesMatchScore = likesMatchScore,
    vibeMatchScore = vibeMatchScore,
    dislikesMatchScore = dislikesMatchScore
)

fun MatchmakingResultDTO.toMatchmakingResult() = MatchmakingResult(
    overallMatchScore = overallMatchScore,
    likesMatchScore = likesMatchScore,
    vibeMatchScore = vibeMatchScore,
    dislikesMatchScore = dislikesMatchScore
)

fun ActiveConnectionEntity.toActiveConnection() = ActiveConnection(
    target = appJson.decodeFromString<MatchmakingBeaconDTO>(matchmakingBeaconDtoString)
        .toMatchmakingBeacon(),
    status = ConnectionStatus.valueOf(connectionStatus),
    encryptedUnreadMessage = encryptedMessagePayload
)





