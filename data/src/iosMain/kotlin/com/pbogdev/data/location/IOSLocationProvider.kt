package com.pbogdev.data.location

import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.location.LocationCoordinates
import com.pbogdev.domain.location.LocationProvider
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSLocationProvider : LocationProvider {

    private val locationManager = CLLocationManager().apply {
        // Enforce the Blind Relay requirement: Only ask for ~3km accuracy
        desiredAccuracy = kCLLocationAccuracyReduced
    }

    // Hold a strong reference so Kotlin/Native doesn't garbage collect
    // the delegate before the async OS callback fires.
    private var activeDelegate: CLLocationManagerDelegateProtocol? = null

    // Centralized, class-level cleanup reachable by both the delegate and the coroutine
    private fun stopAndClearHardware() {
        locationManager.stopUpdatingLocation()
        locationManager.delegate = null
        activeDelegate = null
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): CustomResult<LocationCoordinates> {
        return safeResult {
            suspendCancellableCoroutine { continuation ->
                val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                    override fun locationManager(
                        manager: CLLocationManager,
                        didUpdateLocations: List<*>
                    ) {
                        stopAndClearHardware() // Reached perfectly

                        val location = didUpdateLocations.lastOrNull() as? CLLocation
                        if (location != null) {
                            val coordinates = location.coordinate.useContents {
                                LocationCoordinates(latitude, longitude)
                            }
                            if (continuation.isActive) {
                                continuation.resume(CustomResult.Success(coordinates))
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resume(
                                    CustomResult.Failure(
                                        CustomError.LocationUnavailable("iOS location data was empty or unavailable.")
                                    )
                                )
                            }
                        }
                    }

                    override fun locationManager(
                        manager: CLLocationManager,
                        didFailWithError: NSError
                    ) {
                        stopAndClearHardware() // Reached perfectly

                        if (continuation.isActive) {
                            if (didFailWithError.domain == kCLErrorDomain &&
                                didFailWithError.code == kCLErrorLocationUnknown) {
                                continuation.resume(
                                    CustomResult.Failure(
                                        CustomError.LocationUnavailable("iOS GPS signal temporarily unavailable.")
                                    )
                                )
                            } else {
                                // Throw so safeResult maps to UnknownError
                                continuation.resumeWith(
                                    Result.failure(Exception(didFailWithError.localizedDescription))
                                )
                            }
                        }
                    }
                }

                // Assign the strong reference before handing it to iOS
                activeDelegate = delegate
                locationManager.delegate = delegate

                // Trigger the hardware request
                locationManager.requestLocation()

                // If the coroutine is cancelled (e.g. user leaves screen), clean up!
                continuation.invokeOnCancellation {
                    stopAndClearHardware() // Reached perfectly
                }
            }
        }
    }
}
