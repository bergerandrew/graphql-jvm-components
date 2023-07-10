package me.andrewberger.graphql.config.wiringfactories.typeresolvers

import me.andrewberger.graphql.annotations.DataFetchersResolvedType
import me.andrewberger.graphql.dataFetchers.DataFetchers
import graphql.schema.TypeResolver
import graphql.schema.idl.InterfaceWiringEnvironment
import graphql.schema.idl.UnionWiringEnvironment
import graphql.schema.idl.WiringFactory

/**
 * Provides a [WiringFactory] that retrieves a [graphql.schema.TypeResolver] for identifying the concrete type of Union and Interface types via [DataFetchers] classes annotated with [me.andrewberger.graphql.annotations.DataFetchersResolvedType].
 */
class SchemaResolverTypeResolverWiringFactory(schemaTypeResolvers: Map<String, DataFetchers>, private val schemaResolverTypeResolver: SchemaResolverTypeResolver) : WiringFactory {

    private val resolvedTypeAnnotations = schemaTypeResolvers.map { (beanName, datafetchers) ->
        datafetchers.javaClass.getAnnotation(DataFetchersResolvedType::class.java) }.filterNotNull()

    private val unionTypeNames = resolvedTypeAnnotations.flatMap { it.unionTypes.toList() }
    private val interfaceTypeNames = resolvedTypeAnnotations.flatMap { it.interfaceTypes.toList() }

    override fun getTypeResolver(environment: InterfaceWiringEnvironment): TypeResolver {
        return schemaResolverTypeResolver
    }

    override fun getTypeResolver(environment: UnionWiringEnvironment): TypeResolver {
        return schemaResolverTypeResolver
    }

    override fun providesTypeResolver(environment: InterfaceWiringEnvironment): Boolean {
        return interfaceTypeNames.contains(environment.interfaceTypeDefinition.name)
    }

    override fun providesTypeResolver(environment: UnionWiringEnvironment): Boolean {
        return unionTypeNames.contains(environment.unionTypeDefinition.name)
    }
}