package me.andrewberger.graphql.dataFetchers

import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Interface used to configure a class to provide an entities referenceResolver.
 *
 * Reference resolvers enable entities to be referenced by other services.
 * They are called whenever a query references an entity across service boundaries.
 *
 * @see <a href="https://www.apollographql.com/docs/apollo-server/federation/entities/#extending">Apollo Docs</a>
 */
interface FederatedEntity<T : Any> {
    /**
     * Returns an object containing any fields with corresponding values that are specified by the @key directive.
     * Additionally, this interface should return any values that are added to this type by the services own schema.  In other words, it **should** hydrate
     * the fields in the types schema that don't include an @external directive, because they originate in _this_ service.
     *
     * Any regular resolvers for the type specified will be called as needed for queried fields.
     */
    fun resolveReference(arguments: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<T>

}