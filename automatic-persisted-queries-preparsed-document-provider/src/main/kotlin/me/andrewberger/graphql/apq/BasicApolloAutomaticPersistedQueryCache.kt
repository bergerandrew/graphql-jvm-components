package me.andrewberger.graphql.apq

/**
 * Basic APQ cache implementation for use with [ApolloPersistedQuerySupport][graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport].
 * Uses [BasicApolloAutomaticPersistedQueryCacheRepository]
 *
 * Example:
 * ```
 *      GraphQL.newGraphQL(graphQLSchema)
 *          ...
 *          .preparsedDocumentProvider(ApolloPersistedQuerySupport(BasicApolloAutomaticPersistedQueryCache()))
 *          .build()
 * ```
 */
class BasicApolloAutomaticPersistedQueryCache : ApolloAutomaticPersistedQueryCache(BasicApolloAutomaticPersistedQueryCacheRepository())