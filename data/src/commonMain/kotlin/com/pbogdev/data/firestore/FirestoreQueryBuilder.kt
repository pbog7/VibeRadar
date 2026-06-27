package com.pbogdev.data.firestore

import com.pbogdev.data.firestore.models.ArrayValue
import com.pbogdev.data.firestore.models.ArrayValueWrapper
import com.pbogdev.data.firestore.models.CollectionSelector
import com.pbogdev.data.firestore.models.FieldFilter
import com.pbogdev.data.firestore.models.FieldReference
import com.pbogdev.data.firestore.models.Filter
import com.pbogdev.data.firestore.models.QueryRequest
import com.pbogdev.data.firestore.models.StringValue
import com.pbogdev.data.firestore.models.StructuredQuery


class FirestoreQueryBuilder(private val collection: String) {
    private var fieldFilter: FieldFilter? = null

    /**
     * Constructs an "IN" query for Firestore.
     * Example: whereIn("geohash", listOf("sx8d", "sx8e"))
     */
    fun whereIn(field: String, values: Set<String>): FirestoreQueryBuilder {
        fieldFilter = FieldFilter(
            field = FieldReference(field),
            op = "IN",
            value = ArrayValueWrapper(
                arrayValue = ArrayValue(
                    values = values.map { StringValue(it) }
                )
            )
        )
        return this
    }

    /**
     * Compiles the builder into the strict JSON schema required by Google.
     * This is internal so the consumer of the library never sees the DTO.
     */
    fun build(): QueryRequest {
        val currentFilter = fieldFilter ?: throw IllegalStateException("Firestore query must have a where clause.")

        return QueryRequest(
            structuredQuery = StructuredQuery(
                from = listOf(CollectionSelector(collectionId = collection)),
                where = Filter(fieldFilter = currentFilter)
            )
        )
    }
}