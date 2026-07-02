package com.pbogdev.domain.textEmbedder

import com.pbogdev.domain.models.CustomResult

interface VibeTextEmbedder {
    val isInitialized: Boolean

    suspend fun initialize(delegate: TextEmbedderDelegate = TextEmbedderDelegate.CPU, shouldQuantize: Boolean = false, shouldNormalize: Boolean = true)
    suspend fun embed(text:String): CustomResult<FloatArray>
    suspend fun close()

}