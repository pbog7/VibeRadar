package com.pbogdev.data.utils


import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

val appJson = Json { ignoreUnknownKeys = true }
/**
 * Safely encodes an object to a JSON string on the CPU thread.
 * (If already on the CPU thread, Kotlin takes the zero-overhead fast path!)
 */
suspend inline fun <reified T> Json.safeEncodeToString(
    value: T,
    dispatcherProvider: DispatcherProvider
): String = withContext(dispatcherProvider.default) {
    encodeToString(value)
}

/**
 * Safely decodes a JSON string to an object on the CPU thread.
 * (If already on the CPU thread, Kotlin takes the zero-overhead fast path!)
 */
suspend inline fun <reified T> Json.safeDecodeFromString(
    string: String,
    dispatcherProvider: DispatcherProvider
): T = withContext(dispatcherProvider.default) {
    decodeFromString(string)
}