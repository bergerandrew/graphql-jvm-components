package me.andrewberger.graphql.federation

import graphql.language.TypeDefinition

/**
 *
 * @param typeName The name of the type (__typename) used by the entity
 * @param resolver The reference resolver used to provide instances of this entity
 * @param resolvedType The type returned by the reference resolver
 */
class EntityWiring(val typeName: String, val resolver: EntityDataFetcher<*>, val resolvedType: Class<*>) {

    constructor(
        typeDefinition: TypeDefinition<*>, resolver: EntityDataFetcher<*>, resolvedType: Class<*> ) :
            this(typeDefinition.name, resolver, resolvedType) {
    }
}
