package com.pbogdev.aimatchmakingengine

import cocoapods.MediaPipeTasksText.*
import cocoapods.MediaPipeTasksText.MPPTextEmbedder
import com.pbogdev.aimatchmakingengine.MediaPipeDelegate.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSNumber
import kotlin.concurrent.Volatile

@OptIn(ExperimentalForeignApi::class)
internal class IOSVibeTextEmbedder : VibeTextEmbedder {
    @Volatile
    private var textEmbedder: MPPTextEmbedder? = null
    private val mutex = Mutex()

    @Volatile
    override var isInitialized: Boolean = false
        private set


    override suspend fun initialize(
        delegate: MediaPipeDelegate,
        shouldQuantize: Boolean,
        shouldNormalize: Boolean
    ) {
        if (textEmbedder != null) return
        withContext(Dispatchers.Default) {
            mutex.withLock {
                if (textEmbedder != null) return@withLock
                val modelPath = NSBundle.mainBundle.pathForResource(
                    name = "bert_embedder",
                    ofType = "tflite",
                    inDirectory = "compose-resources/composeResources/viberadar.aimatchmakingengine.generated.resources/files"
                )
                println("Model path is $modelPath")
                if (modelPath == null) {
                    return@withLock
                } else {
                    val textEmbedderOptions = MPPTextEmbedderOptions()
                    textEmbedderOptions.apply {
                        quantize = shouldQuantize
                        l2Normalize = shouldNormalize
                        baseOptions.modelAssetPath = modelPath
                        baseOptions.setDelegate(
                            when (delegate) {
                                CPU -> MPPDelegate.MPPDelegateCPU
                                GPU -> MPPDelegate.MPPDelegateGPU
                            }
                        )
                    }
                    textEmbedder = MPPTextEmbedder(options = textEmbedderOptions, error = null)
                    isInitialized = textEmbedder!=null
                }
            }
        }
    }

    override suspend fun embed(text: String): FloatArray? {
        if (text.isBlank()) return null
        if (textEmbedder == null) {
            initialize()
        }
        return withContext(Dispatchers.Default) {
            mutex.withLock {
                try {
                    val result = textEmbedder?.embedText(text, error = null)
                    // MediaPipe supports models that output multiple embeddings at once but here a model with only a single output is used
                    val embeddingObject = result?.embeddingResult()
                        ?.embeddings()
                        ?.firstOrNull() as? MPPEmbedding
                    val rawValues = embeddingObject?.floatEmbedding
                    rawValues?.map {
                        (it as NSNumber).floatValue
                    }?.toFloatArray()

                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

    }

    override fun close() {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                textEmbedder = null
                isInitialized = false
            }
        }
    }
}