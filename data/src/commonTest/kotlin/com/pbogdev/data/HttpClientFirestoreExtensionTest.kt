package com.pbogdev.data



import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.data.firestore.wrapperModels.ArrayValueWrapper
import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import com.pbogdev.data.firestore.queryFirestore
import com.pbogdev.testcore.TestDispatcherProvider
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Serializable
internal data class TestDto(
    val title: StringValue
)

@Serializable
internal data class ComplexTestDto(
    val payload: StringValue,
    val expiresAt: TimestampValue,
    val tags: ArrayValueWrapper,          // Tests double-nested arrays
    val optionalNote: StringValue? = null // Tests missing keys/nullability
)

class FirestoreKtorExtTest {
    private val testDispatcherProvider: DispatcherProvider = TestDispatcherProvider()
    // Helper function to create a fake Ktor client
    private fun createMockClient(
        responseJson: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    assertTrue(request.url.toString().contains("v1/projects/test-project/databases"))

                    respond(
                        content = ByteReadChannel(responseJson),
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }
    }

    // --- Basic & Validation Tests ---

    @Test
    fun `queryFirestore_successfully_parses_valid Google_gRPC_JSON`() = runTest {
        val validJson = """
            [
              {
                "document": {
                  "name": "projects/test-project/databases/(default)/documents/TestCollection/doc_789",
                  "fields": {
                    "title": { "stringValue": "Hello KMP" }
                  }
                }
              }
            ]
        """.trimIndent()

        val client = createMockClient(validJson)
        val result = client.queryFirestore<TestDto>(
            projectId = "test-project", 
            collection = "TestCollection", 
            dispatcherProvider = testDispatcherProvider
        ) {
            whereIn("some_field", setOf("value1"))
        }

        assertEquals(1, result.size)
        assertEquals("doc_789", result[0].id)
        assertEquals("Hello KMP", result[0].data.title.stringValue)
    }

    @Test
    fun `queryFirestore_throws Exception_if_builder_is_missing_where_clause`() = runTest {
        val client = createMockClient("[]")
        val exception = assertFailsWith<IllegalStateException> {
            client.queryFirestore<TestDto>(
                projectId = "test-project", 
                collection = "TestCollection", 
                dispatcherProvider = testDispatcherProvider
            ) {
                // Intentionally forgetting whereIn()
            }
        }
        assertEquals("Firestore query must have a where clause.", exception.message)
    }

    // --- Edge Case Tests ---

    @Test
    fun queryFirestore_successfully_parses_complex_nested_fields_arrays_and_missing_values() = runTest {
        // Arrange: Missing 'optionalNote', deeply nested 'tags' array
        val complexJson = """
            [
              {
                "document": {
                  "name": "projects/test-project/databases/(default)/documents/Relays/beacon_999",
                  "fields": {
                    "payload": { "stringValue": "encrypted_byte_string" },
                    "expiresAt": { "timestampValue": "2026-12-31T23:59:59Z" },
                    "tags": { 
                      "arrayValue": { 
                        "values": [ 
                          { "stringValue": "bluetooth" }, 
                          { "stringValue": "active" } 
                        ] 
                      } 
                    }
                  }
                }
              }
            ]
        """.trimIndent()

        val client = createMockClient(complexJson)

        // Act
        val result = client.queryFirestore<ComplexTestDto>(
            projectId = "test-project", 
            collection = "Relays", 
            dispatcherProvider = testDispatcherProvider
        ) {
            whereIn("geohash", setOf("u2x1"))
        }

        // Assert
        assertEquals(1, result.size)
        val data = result[0].data

        assertEquals("beacon_999", result[0].id)
        assertEquals("encrypted_byte_string", data.payload.stringValue)
        assertEquals("2026-12-31T23:59:59Z", data.expiresAt.timeStampValue)
        assertNull(data.optionalNote, "Missing JSON fields should safely parse to null")

        assertEquals(2, data.tags.arrayValue.values.size)
        assertEquals("bluetooth", data.tags.arrayValue.values[0].stringValue)
        assertEquals("active", data.tags.arrayValue.values[1].stringValue)
    }

    @Test
    fun queryFirestore_ignores_unknown_Google_metadata_fields_without_crashing() = runTest {
        // Arrange: Google injects 'createTime' and 'updateTime', which are NOT in our TestDto
        val jsonWithUnknownKeys = """
            [
              {
                "document": {
                  "name": "projects/test-project/databases/(default)/documents/TestCollection/doc_1",
                  "fields": {
                    "title": { "stringValue": "Safe Data" }
                  },
                  "createTime": "2024-01-01T12:00:00Z",
                  "updateTime": "2024-01-02T12:00:00Z"
                }
              }
            ]
        """.trimIndent()

        val client = createMockClient(jsonWithUnknownKeys)

        // Act & Assert (If it doesn't crash, the test passes!)
        val result = client.queryFirestore<TestDto>(
            projectId = "test-project", 
            collection = "TestCollection", 
            dispatcherProvider = testDispatcherProvider
        ) {
            whereIn("some_field", setOf("value1"))
        }

        assertEquals("Safe Data", result[0].data.title.stringValue)
    }

    @Test
    fun queryFirestore_parses_multiple_documents_in_a_single_array() = runTest {
        // Arrange: Two valid documents in the array
        val multiJson = """
            [
              {
                "document": {
                  "name": "projects/test-project/databases/(default)/documents/TestCollection/doc_1",
                  "fields": { "title": { "stringValue": "First" } }
                }
              },
              {
                "document": {
                  "name": "projects/test-project/databases/(default)/documents/TestCollection/doc_2",
                  "fields": { "title": { "stringValue": "Second" } }
                }
              }
            ]
        """.trimIndent()

        val client = createMockClient(multiJson)

        // Act
        val result = client.queryFirestore<TestDto>(
            projectId = "test-project", 
            collection = "TestCollection", 
            dispatcherProvider = testDispatcherProvider
        ) {
            whereIn("some_field", setOf("value1"))
        }

        // Assert
        assertEquals(2, result.size)
        assertEquals("doc_1", result[0].id)
        assertEquals("First", result[0].data.title.stringValue)
        assertEquals("doc_2", result[1].id)
        assertEquals("Second", result[1].data.title.stringValue)
    }

    @Test
    fun queryFirestore_ignores_empty_wrapper_objects_without_crashing() = runTest {
        val emptyJson = """ [ { "readTime": "2023-01-01T00:00:00Z" } ] """
        val client = createMockClient(emptyJson)
        val result = client.queryFirestore<TestDto>(
            projectId = "test-project", 
            collection = "TestCollection", 
            dispatcherProvider = testDispatcherProvider
        ) {
            whereIn("some_field", setOf("value1"))
        }
        assertTrue(result.isEmpty())
    }

    // --- Error Handling Tests ---

    @Test
    fun queryFirestore_throws_Exception_on_400_Bad_Request() = runTest {
        val errorJson = """{ "error": { "code": 400, "message": "FAILED_PRECONDITION" } }"""
        val client = createMockClient(errorJson, HttpStatusCode.BadRequest)

        val exception = assertFailsWith<IllegalStateException> {
            client.queryFirestore<TestDto>(
                projectId = "test-project", 
                collection = "TestCollection", 
                dispatcherProvider = testDispatcherProvider
            ) {
                whereIn("some_field", setOf("value1"))
            }
        }
        assertTrue(exception.message!!.contains("400"))
    }

    @Test
    fun queryFirestore_throws_SerializationException_on_completely_malformed_JSON() = runTest {
        val badJson = """ { "random_garbage": true } """
        val client = createMockClient(badJson)

        assertFailsWith<SerializationException> {
            client.queryFirestore<TestDto>(
                projectId = "test-project", 
                collection = "TestCollection", 
                dispatcherProvider = testDispatcherProvider
            ) {
                whereIn("some_field", setOf("value1"))
            }
        }
    }
}
