package me.andrewberger.graphql.apq

import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiFunction

/**
 * Simple [ApolloAutomaticPersistedQueryCacheRepository] using [ConcurrentHashMap]
 */
class BasicApolloAutomaticPersistedQueryCacheRepository<K, V> : ApolloAutomaticPersistedQueryCacheRepository<K, V> {
    private val map: MutableMap<K, V> = ConcurrentHashMap<K, V>()
    override fun compute(key: K, remappingFunction: BiFunction<in K?, in V?, out V?>): V? {
        return map.compute(key, remappingFunction)
    }
}