package me.andrewberger.graphql.apq

import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQueryCache
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import graphql.execution.preparsed.persisted.PersistedQuerySupport.PERSISTED_QUERY_MARKER

open class ApolloAutomaticPersistedQueryCache(private val apolloAutomaticPersistedQueryCacheRepository: ApolloAutomaticPersistedQueryCacheRepository<String, PreparsedDocumentEntry?>) : PersistedQueryCache {

    override fun getPersistedQueryDocument(persistedQueryId: Any, executionInput: ExecutionInput, onCacheMiss: PersistedQueryCacheMiss): PreparsedDocumentEntry? {
        return apolloAutomaticPersistedQueryCacheRepository.compute(persistedQueryId as String) { key, value ->
            if (value != null) {
                value
            } else {
                val query = executionInput.query
                if (PERSISTED_QUERY_MARKER == query || query == null) {
                    throw PersistedQueryNotFound (persistedQueryId)
                } else {
                    onCacheMiss.apply(query)
                }
            }
        }
    }
}