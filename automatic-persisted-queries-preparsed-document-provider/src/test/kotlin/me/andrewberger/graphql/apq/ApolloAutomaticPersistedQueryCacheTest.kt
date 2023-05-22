package me.andrewberger.graphql.apq

import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import graphql.execution.preparsed.persisted.PersistedQuerySupport
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.function.BiFunction

class ApolloAutomaticPersistedQueryCacheTest {

    val QUERY_TYPENAME: String = "{__typename}"
    val QUERY_TYPNEAME_HASH: String = "ecf4edb46db40b5132295c0291d62fb65d6759a9eedfa4d5d612dd5ec54a6b38"

    private fun buildExtensions(hash: String): Map<String, Any> = mapOf("persistedQuery" to mapOf("version" to 1, "sha256Hash" to hash))

    @Test
    fun persistedQueryNotFoundTest() {
        val persistedQueryId = "foo"
        val executionInput = ExecutionInput.newExecutionInput(PersistedQuerySupport.PERSISTED_QUERY_MARKER)
            .extensions(buildExtensions(QUERY_TYPNEAME_HASH))
            .build()
        val repository = object : ApolloAutomaticPersistedQueryCacheRepository<String, PreparsedDocumentEntry?> {
            override fun compute(key: String, remappingFunction: BiFunction<in String?, in PreparsedDocumentEntry?, out PreparsedDocumentEntry?>): PreparsedDocumentEntry? {
                return remappingFunction.apply(key, null)
            }
        }

        val apolloAPQCache = ApolloAutomaticPersistedQueryCache(repository)
        val persistedQueryDocumentResult = kotlin.runCatching {
            apolloAPQCache.getPersistedQueryDocument(persistedQueryId, executionInput) {
                throw Exception("onCacheMiss called but PersistedQueryNotFound should have been thrown")
            }
        }

        val exception = persistedQueryDocumentResult.exceptionOrNull()
        assertEquals(PersistedQueryNotFound::class, exception!!::class)
        assertEquals(persistedQueryId, (exception as PersistedQueryNotFound).persistedQueryId)
    }


    @Test
    fun cacheMissTest() {
        val persistedQueryId = "foo"
        val executionInput = ExecutionInput.newExecutionInput(QUERY_TYPENAME)
            .extensions(buildExtensions(QUERY_TYPNEAME_HASH))
            .build()
        val repository = object : ApolloAutomaticPersistedQueryCacheRepository<String, PreparsedDocumentEntry?> {
            override fun compute(key: String, remappingFunction: BiFunction<in String?, in PreparsedDocumentEntry?, out PreparsedDocumentEntry?>): PreparsedDocumentEntry? {
                return remappingFunction.apply(key, null)
            }
        }

        val preparsedDocumentEntry = mockkClass(PreparsedDocumentEntry::class)
        val onCacheMiss = mockkClass(PersistedQueryCacheMiss::class)
        every { onCacheMiss.apply(QUERY_TYPENAME) } returns preparsedDocumentEntry


        val apolloAPQCache = ApolloAutomaticPersistedQueryCache(repository)
        val persistedQueryDocumentResult = kotlin.runCatching {
            apolloAPQCache.getPersistedQueryDocument(persistedQueryId, executionInput, onCacheMiss)
        }

        verify(exactly = 1) { onCacheMiss.apply(QUERY_TYPENAME) }
        assertEquals(preparsedDocumentEntry, persistedQueryDocumentResult.getOrNull())
    }


    @Test
    fun cacheHitTest() {
        val persistedQueryId = "foo"
        val executionInput = ExecutionInput.newExecutionInput(PersistedQuerySupport.PERSISTED_QUERY_MARKER)
            .extensions(buildExtensions(QUERY_TYPNEAME_HASH))
            .build()
        val preparsedDocumentEntry = mockkClass(PreparsedDocumentEntry::class)
        val repository = object : ApolloAutomaticPersistedQueryCacheRepository<String, PreparsedDocumentEntry?> {
            override fun compute(key: String, remappingFunction: BiFunction<in String?, in PreparsedDocumentEntry?, out PreparsedDocumentEntry?>): PreparsedDocumentEntry? {
                return remappingFunction.apply(key, preparsedDocumentEntry)
            }
        }

        val apolloAPQCache = ApolloAutomaticPersistedQueryCache(repository)
        val persistedQueryDocumentResult = kotlin.runCatching {
            apolloAPQCache.getPersistedQueryDocument(persistedQueryId, executionInput) {
                throw Exception("onCacheMiss called but cached value should have been returned")
            }
        }

        val resultDocument = persistedQueryDocumentResult.getOrNull()
        assertEquals(preparsedDocumentEntry, resultDocument)
    }
}