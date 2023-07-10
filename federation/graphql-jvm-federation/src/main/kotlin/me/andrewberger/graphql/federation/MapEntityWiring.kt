package me.andrewberger.graphql.federation

import graphql.language.TypeDefinition

/**
 *
 * @param typeName The name of the type (__typename) used by the entity
 * @param resolver The reference resolver used to provide instances of this entity
 */
class MapEntityWiring(val typeName: String, val resolver: EntityDataFetcher<Map<String, *>>) {

    constructor(typeDefinition: TypeDefinition<*>, resolver: EntityDataFetcher<Map<String, *>>) : this(typeDefinition.name, resolver) {
    }
}
