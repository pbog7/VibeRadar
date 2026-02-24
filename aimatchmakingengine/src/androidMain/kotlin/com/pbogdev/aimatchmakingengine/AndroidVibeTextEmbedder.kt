package com.pbogdev.aimatchmakingengine

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions
import com.pbogdev.aimatchmakingengine.MediaPipeDelegate.CPU
import com.pbogdev.aimatchmakingengine.MediaPipeDelegate.GPU
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class AndroidVibeTextEmbedder(private val context: Context) : VibeTextEmbedder {
    @Volatile
    private var textEmbedder: TextEmbedder? = null

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
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (textEmbedder != null) return@withLock
                try {
                    val baseOptions = BaseOptions.builder()
                        .setModelAssetPath("composeResources/viberadar.aimatchmakingengine.generated.resources/files/bert_embedder.tflite")
                        .setDelegate(
                            when (delegate) {
                                CPU -> Delegate.CPU
                                GPU -> Delegate.GPU
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

    override suspend fun embed(text: String): FloatArray? {
        if (text.isBlank()) return null
        if (textEmbedder == null) {
            initialize()
        }
        return withContext(Dispatchers.Default) {
            mutex.withLock {
                try {
                    val result = textEmbedder?.embed(text)
                    // MediaPipe supports models that output multiple embeddings at once but here a model with only a single output is used
                    val embedding = result?.embeddingResult()?.embeddings()?.firstOrNull()
                    embedding?.floatEmbedding()
                } catch (e: Exception) {
                    println("Embed Error: ${e.message}")
                    null
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun close() {
        if (!isInitialized) {
            println("Text embedder not initialized returning from close")
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // We use the mutex to ensure no 'embed' is currently
                // mid-calculation with the pointer
                mutex.withLock {
                    println("BERT: Starting native close...")
                    textEmbedder?.close()
                    textEmbedder = null
                    println("BERT: Native close finished!") // This is the one failing
                }
            } catch (e: Exception) {
                println("BERT: Native close failed: ${e.message}")
            }
        }
    }
}
