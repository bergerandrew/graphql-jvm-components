package me.andrewberger.graphql.config.wiringfactories.typeresolvers

import me.andrewberger.graphql.annotations.DataFetchersFor
import me.andrewberger.graphql.annotations.DataFetchersResolvedType
import me.andrewberger.graphql.dataFetchers.DataFetchers
import graphql.TypeResolutionEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.TypeResolver

/**
 * [graphql.schema.TypeResolver] used by [SchemaResolverTypeResolverWiringFactory]
 */
class SchemaResolverTypeResolver(schemaTypeResolvers: Map<String, DataFetchers>) : TypeResolver {

    val classToTypeMap = schemaTypeResolvers.map { (beanName, datafetchers) ->
        val dataFetcherComponent = datafetchers.javaClass.getAnnotation(DataFetchersFor::class.java)
        val typeDefinitionName = dataFetcherComponent.schemaType
        val resolvedTypeAnnotation = datafetchers.javaClass.getAnnotation(DataFetchersResolvedType::class.java)
//        val (unionTypeNames, interfaceTypeNames) = resolvedTypeAnnotations.map { it.unionTypes to it.interfaceTypes }
        if (resolvedTypeAnnotation != null) {
            val schemaTypeClass = resolvedTypeAnnotation.schemaTypeClass

            schemaTypeClass to typeDefinitionName
        } else {
            null
        }
    }.filterNotNull().toMap()

    override fun getType(typeResolutionEnvironment: TypeResolutionEnvironment): GraphQLObjectType {
        val objectToResolve = typeResolutionEnvironment.getObject<Any>()
        val objectToResolveClass = objectToResolve::class
        val typeDefinitionName = classToTypeMap[objectToResolveClass]

        return typeResolutionEnvironment.schema.getObjectType(typeDefinitionName)
    }
}