package me.andrewberger.graphql.config.wiringfactories

import me.andrewberger.graphql.config.wiringfactories.datafetchers.SchemaResolverDataFetcherWiringFactory
import me.andrewberger.graphql.config.wiringfactories.typeresolvers.SchemaResolverTypeResolver
import me.andrewberger.graphql.config.wiringfactories.typeresolvers.SchemaResolverTypeResolverWiringFactory
import me.andrewberger.graphql.dataFetchers.DataFetchers
import graphql.schema.idl.WiringFactory
import org.springframework.stereotype.Component

/**
 * Container for [DataFetchers], [me.andrewberger.graphql.annotations.DataFetchersFor], and [me.andrewberger.graphql.annotations.FieldDataFetcher] configuration.
 *
 * - Provides a [WiringFactory] that retrieves [graphql.schema.DataFetcher]s for resolving fields annotated with [me.andrewberger.graphql.annotations.FieldDataFetcher] on a class implementing [DataFetchers].
 * - Provides a [WiringFactory] that retrieves a [graphql.schema.TypeResolver] for identifying the concrete type of Union and Interface types via [DataFetchers] classes annotated with [me.andrewberger.graphql.annotations.DataFetchersResolvedType].
 */
@Component
class SchemaResolverConfig(resolvers: Map<String, DataFetchers>) {

    val schemaResolverDataFetcherWiringFactory = SchemaResolverDataFetcherWiringFactory(resolvers)

    val schemaResolverTypeResolverWiringFactory = SchemaResolverTypeResolverWiringFactory(resolvers, SchemaResolverTypeResolver(resolvers))

    fun getDataFetcherWiringFactory(): WiringFactory {
        return schemaResolverDataFetcherWiringFactory
    }

    fun getTypeResolverWiringFactory(): WiringFactory {
        return schemaResolverTypeResolverWiringFactory
    }

}