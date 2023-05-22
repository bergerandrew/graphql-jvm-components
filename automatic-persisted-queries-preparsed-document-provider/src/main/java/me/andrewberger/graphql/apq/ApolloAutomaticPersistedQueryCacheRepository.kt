package me.andrewberger.graphql.apq

import java.util.function.BiFunction

/**
 * Interface to cache, compute, and retrieve the required value.  Compute is called with the persisted query id as key and the key and existing value
 * are provided to the apply method on the remappingFunction.  The result should then be persisted as the new value.  Implementations must be thread safe.
 */
interface ApolloAutomaticPersistedQueryCacheRepository<K,V> {
    /**
     * @param key persisted query id
     * @param remappingFunction to be called to compute/apply the value based off of the existing value. The returned value represents the new value for
     * the key and should be persisted.
     */
    fun compute(key: K, remappingFunction: BiFunction<in K?, in V?, out V?>) : V?
}