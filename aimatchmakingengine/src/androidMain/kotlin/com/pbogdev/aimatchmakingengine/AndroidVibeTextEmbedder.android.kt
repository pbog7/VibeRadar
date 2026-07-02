package com.pbogdev.aimatchmakingengine

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.textEmbedder.TextEmbedderDelegate
import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class AndroidVibeTextEmbedder(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : VibeTextEmbedder {
    @Volatile
    private var textEmbedder: TextEmbedder? = null

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
        withContext(dispatcherProvider.io) {
            mutex.withLock {
                if (textEmbedder != null) return@withLock
                try {
                    val baseOptions = BaseOptions.builder()
                        .setModelAssetPath("composeResources/viberadar.aimatchmakingengine.generated.resources/files/bert_embedder.tflite")
                        .setDelegate(
                            when (delegate) {
                                TextEmbedderDelegate.CPU -> Delegate.CPU
                                TextEmbedderDelegate.GPU -> Delegate.GPU
                            }
                        )
                        .build()

                    val options = TextEmbedderOptions.builder()
                        .setBaseOptions(baseOptions)
                        .setQuantize(shouldQuantize)
                        .setL2Normalize(shouldNormalize)
                        .build()
                    textEmbedder = TextEmbedder.createFromOptions(context, options)
                    println("BERT Embedder initialized successfully on Android!")
                    isInitialized = true

                } catch (e: Exception) {
                    isInitialized = false
                    println("Error initializing BERT Embedder: ${e.message}")
                    e.printStackTrace()
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
                    val result =
                        textEmbedder?.embed(text) ?: return@safeResult CustomResult.Failure(
                            CustomError.TextEmbedderInitializationError()
                        )
                    // MediaPipe supports models that output multiple embeddings at once but here a model with only a single output is used
                    val embedding = result.embeddingResult().embeddings().first()
                    CustomResult.Success(embedding.floatEmbedding())
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun close() {
        if (!isInitialized) {
            println("Text embedder not initialized returning from close")
            return
        }
        withContext(dispatcherProvider.io + NonCancellable) {
            try {
                // We use the mutex to ensure no 'embed' is currently
                // mid-calculation with the pointer
                mutex.withLock {
                    println("BERT: Starting native close...")
                    textEmbedder?.close()
                    textEmbedder = null
                    isInitialized = false
                    println("BERT: Native close finished!") // This is the one failing
                }
            } catch (e: Exception) {
                println("BERT: Native close failed: ${e.message}")
            }
        }
    }
}
