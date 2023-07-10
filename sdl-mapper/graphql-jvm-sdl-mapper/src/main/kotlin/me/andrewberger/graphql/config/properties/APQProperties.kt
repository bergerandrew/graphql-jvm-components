package me.andrewberger.graphql.config.properties

import me.andrewberger.graphql.apq.BasicApolloAutomaticPersistedQueryCache
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport
import graphql.execution.preparsed.persisted.PersistedQueryCache
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "me.andrewberger.graphql.apq")
class APQProperties {
    //KDoc won't work here since we want the doc in the spring-configuration-metadata.json
    /**
     * Enables support for <a href="https://www.apollographql.com/docs/apollo-server/performance/apq/">Automatic persisted queries</a>
     * (APQ) via a [BasicApolloAutomaticPersistedQueryCache].  Alternatively, provide a custom [PersistedQueryCache] implementation.
     * <br>
     * <br>
     * <b>Note:</b> Cannot be used in combination with a [PreparsedDocumentProvider] as APQ support is provided by using
     * [ApolloPersistedQuerySupport] as a [PreparsedDocumentProvider].
     */
    var enabled: Boolean? = true
}