package com.pbogdev.testcore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
/**
 * A test implementation of DispatcherProvider.
 * By routing all dispatchers to a single TestDispatcher, we eliminate thread-switching
 * delays and make our unit tests run instantly and deterministically.
 */
class TestDispatcherProvider @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}