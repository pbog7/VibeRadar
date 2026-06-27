package com.pbogdev.data.utils

import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlin.coroutines.cancellation.CancellationException

/**
 * A helper function to safely execute suspend functions,
 * automatically handling CancellationExceptions and mapping generic errors.
 */
inline fun <T> safeResult(
    // Optional mapper: Takes an Exception, returns a CustomError. Defaults to returning null.
    mapException: (Exception) -> CustomError? = { null },
    block: () -> CustomResult<T>
): CustomResult<T> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // If the mapper provides a specific error, use it. Otherwise, use UnknownError.
        val error = mapException(e) ?: CustomError.UnknownError(e.message ?: "Unknown Error")
        CustomResult.Failure(error)
    }
}