package me.andrewberger.graphql.annotations

import kotlin.reflect.KClass


/**
 * Marks the specified class as being of the type specified in a [DataFetchersFor] annotations [DataFetchersFor.schemaType]
 */
annotation class DataFetchersResolvedType (val schemaTypeClass: KClass<*>, val unionTypes: Array<String> = [],
                                           val interfaceTypes : Array<String> = [])//, val useForFederation: Boolean = true)