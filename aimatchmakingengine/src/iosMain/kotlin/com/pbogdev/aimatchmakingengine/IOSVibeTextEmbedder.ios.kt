package com.pbogdev.aimatchmakingengine

import cocoapods.MediaPipeTasksText.*
import cocoapods.MediaPipeTasksText.MPPTextEmbedder
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.textEmbedder.TextEmbedderDelegate
import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSNumber
import kotlin.concurrent.Volatile

@OptIn(ExperimentalForeignApi::class)
internal class IOSVibeTextEmbedder(private val dispatcherProvider: DispatcherProvider) :
    VibeTextEmbedder {
    @Volatile
    private var textEmbedder: MPPTextEmbedder? = null
    private val mutex = Mutex()

    @Volatile
    override var isInitialized: Boolean = false
        private set


    override suspend fun initialize(
        delegate: TextEmbedderDelegate,
        shouldQuantize: Boolean,
        shouldNormalize: Boolean
    ) {
        if (textEmbedder != null) return
        withContext(dispatcherProvider.default) {
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
                                TextEmbedderDelegate.CPU -> MPPDelegate.MPPDelegateCPU
                                TextEmbedderDelegate.GPU -> MPPDelegate.MPPDelegateGPU
                            }
                        )
                    }
                    textEmbedder = MPPTextEmbedder(options = textEmbedderOptions, error = null)
                    isInitialized = textEmbedder != null
                }
            }
        }
    }

    override suspend fun embed(text: String): CustomResult<FloatArray> {
        if (text.isBlank()) return CustomResult.Failure(CustomError.BlankTextError())
        if (textEmbedder == null) {
            initialize()
        }
        return withContext(dispatcherProvider.default) {
            mutex.withLock {
                safeResult(mapException = { e ->
                    CustomError.EmbeddingError(
                        e.message ?: "Embedding error"
                    )
                }
                )
                {
                    val result = textEmbedder?.embedText(text, error = null)
                    // MediaPipe supports models that output multiple embeddings at once but here a model with only a single output is used
                    val embeddingObject = result?.embeddingResult()
                        ?.embeddings()
                        ?.firstOrNull() as? MPPEmbedding
                    val rawValues = embeddingObject!!.floatEmbedding!!
                    val vector = rawValues.map {
                        (it as NSNumber).floatValue
                    }.toFloatArray()
                    CustomResult.Success(vector)
                }
            }
        }

    }

    override suspend fun close() {
        withContext(dispatcherProvider.io + NonCancellable) {
            mutex.withLock {
                textEmbedder = null
                isInitialized = false
            }
        }
    }
}