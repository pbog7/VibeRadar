package com.pbogdev.aimatchmakingengine

interface VibeTextEmbedder {
    val isInitialized: Boolean

    suspend fun initialize(delegate: MediaPipeDelegate = MediaPipeDelegate.CPU, shouldQuantize: Boolean = false, shouldNormalize: Boolean = true)
    suspend fun embed(text:String): FloatArray?
    fun close()

}

