package com.pbogdev.data.repository

import co.touchlab.kermit.Logger
import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.dispatcherProvider.DispatcherProvider
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.appJson
import com.pbogdev.data.network.dto.BeaconDto
import com.pbogdev.data.toBeacon
import com.pbogdev.data.utils.safeResult
import com.pbogdev.domain.LocationManager
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class BeaconRepositoryImpl(
    private val locationManager: LocationManager,
    private val apiService: ApiService,
    private val cryptoEngine: CryptographyEngine,
    private val dispatcherProvider: DispatcherProvider
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
                    Logger.i("DTO is $dto")
                    async {
                        val decryptResult = cryptoEngine.decrypt(
                            encryptedBase64 = dto.payloadBase64,
                            geohash = dto.geohash,
                            timeWindow = dto.expiresAt.toString()
                        )
                        Logger.i("Decrypt result is $decryptResult")
                        if (decryptResult is CustomResult.Success) {
                            Logger.i("decryptresult data ${decryptResult.data}")
                            val decryptedJson = decryptResult.data
                            val beaconDto = appJson.decodeFromString<BeaconDto>(decryptedJson)
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
}