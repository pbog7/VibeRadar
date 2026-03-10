package com.pbogdev.data

import com.pbogdev.data.network.dto.BeaconDto
import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.dto.ProfileDto
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.ExampleModel
import com.pbogdev.domain.models.Profile


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
    profile = profile.toProfile(),
    vibeVector = vibeVector,
    timestamp = timestamp
)



fun Beacon.toBeaconDto() = BeaconDto(
    beaconId = beaconId,
    profile = profile.toProfileDto(),
    vibeVector = vibeVector,
    timestamp = timestamp
)

