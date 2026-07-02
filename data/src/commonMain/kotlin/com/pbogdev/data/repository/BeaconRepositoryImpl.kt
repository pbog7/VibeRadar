package com.pbogdev.data.repository

import co.touchlab.kermit.Logger
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.epochMillisToIso
import com.pbogdev.core.utils.safeResult
import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.firestore.documentFieldsModels.EncryptedBeaconFields
import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.dto.BeaconDto
import com.pbogdev.data.toBeacon
import com.pbogdev.data.toBeaconDto
import com.pbogdev.data.utils.appJson
import com.pbogdev.data.utils.safeDecodeFromString
import com.pbogdev.data.utils.safeEncodeToString
import com.pbogdev.domain.LocationManager
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class BeaconRepositoryImpl(
    private val locationManager: LocationManager,
    private val apiService: ApiService,
    private val cryptoEngine: CryptographyEngine,
    private val dispatcherProvider: DispatcherProvider,
    private val authenticator: AnonymousAuthenticator
) : BeaconRepository {


    override suspend fun getNearbyBeacons(): CustomResult<List<Beacon>> = safeResult {
        // Fetch current geohash grid from Datastore
        val geohashResult = locationManager.fetchCurrentGeohashGrid()
        if (geohashResult is CustomResult.Failure) {
            return CustomResult.Failure(geohashResult.error)
        } else {
            // Query firestore for nearby encrypted beacons
            val geohashGrid = (geohashResult as CustomResult.Success).data
            val encryptedBeaconDTOList = apiService.getBeaconsByGeohashes(geohashGrid)

            // decrypt encrypted beacons and map them into domain beacons asynchronously
            val beacons = withContext(dispatcherProvider.default) {
                encryptedBeaconDTOList.map { dto ->
                    Logger.i("DTO geohash:${dto.geohash}, expiresAtEpochMillis:${dto.expiresAtEpochMillis}")
                    async {
                        val decryptResult = cryptoEngine.decrypt(
                            encryptedBase64 = dto.payloadBase64,
                            geohash = dto.geohash,
                            expiresAtEpochMillis = dto.expiresAtEpochMillis
                        )
                        Logger.i("Decrypt result is $decryptResult")
                        if (decryptResult is CustomResult.Success) {
                            Logger.i("decryptresult data ${decryptResult.data}")
                            val decryptedJson = decryptResult.data
                            val beaconDto = appJson.safeDecodeFromString<BeaconDto>(
                                dispatcherProvider = dispatcherProvider,
                                string = decryptedJson
                            )
                            beaconDto.toBeacon()
                        } else {
                            // Skip beacons that fail decryption
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            CustomResult.Success(beacons)
        }
    }

    override suspend fun uploadBeacon(beacon: Beacon): CustomResult<Unit> = safeResult {
        val geohashResult = locationManager.fetchCurrentGeohash()
        if (geohashResult is CustomResult.Failure) {
            return CustomResult.Failure(geohashResult.error)
        } else {
            val userId = authenticator.getCurrentUid() ?: return CustomResult.Failure(CustomError.UserNotLoggedIn())
            val geohash = (geohashResult as CustomResult.Success).data
            val encryptBeaconResult = cryptoEngine.encrypt(
                payloadAsString = appJson.safeEncodeToString(
                    dispatcherProvider = dispatcherProvider,
                    value = beacon.toBeaconDto()
                ),
                geohash = geohash,
                expiresAtEpochMillis = beacon.expiresAt
            )
            when (encryptBeaconResult) {
                is CustomResult.Failure -> {
                    return@safeResult CustomResult.Failure(encryptBeaconResult.error)
                }

                is CustomResult.Success -> {
                    apiService.uploadBeacon(
                        EncryptedBeaconFields(
                            geohash = StringValue(geohash),
                            expiresAt = TimestampValue(epochMillisToIso(beacon.expiresAt)),
                            payload = StringValue(encryptBeaconResult.data),
                            expiresAtEpochMillis = StringValue(beacon.expiresAt.toString()),
                            senderUid = StringValue(userId)
                        )
                    )
                }
            }
            CustomResult.Success(Unit)
        }
    }
}