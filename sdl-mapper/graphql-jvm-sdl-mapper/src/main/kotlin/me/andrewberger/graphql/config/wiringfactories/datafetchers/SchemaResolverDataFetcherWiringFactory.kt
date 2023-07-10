package me.andrewberger.graphql.config.wiringfactories.datafetchers

import me.andrewberger.graphql.annotations.FieldDataFetcher
import me.andrewberger.graphql.annotations.DataFetchersFor
import me.andrewberger.graphql.dataFetchers.DataFetchers
import graphql.schema.DataFetcher
import graphql.schema.idl.FieldWiringEnvironment
import graphql.schema.idl.WiringFactory
import org.slf4j.LoggerFactory
import kotlin.reflect.jvm.isAccessible


private val logger = LoggerFactory.getLogger(SchemaResolverDataFetcherWiringFactory::class.java)

/**
 * Provides a [WiringFactory] that retrieves [graphql.schema.DataFetcher]s for resolving fields annotated with [me.andrewberger.graphql.annotations.FieldDataFetcher] on a class implementing [DataFetchers].
 */
class SchemaResolverDataFetcherWiringFactory(resolvers: Map<String, DataFetchers>): WiringFactory {

    //Map of each graph types name to another map of the types fields to datafetcher for each field
    //TypeName {
    //      FieldName
    //      FieldDataFetcher
    //}
    val typeToFieldDataFetcherMap: Map<String, Map<String, DataFetcher<*>>> = resolvers.map { (beanName, resolver) ->
        val resolverAnnotation = resolver.javaClass.getAnnotation(DataFetchersFor::class.java)
        val type = resolverAnnotation.schemaType


        val fieldNameToDataFetcher = resolver::class.members.mapNotNull { method ->
            val resolverField = method.annotations.find { it is FieldDataFetcher } as FieldDataFetcher?
            if (resolverField != null) {
                val fieldName = resolverField.fieldName.takeUnless { it.isBlank() } ?: method.name.removePrefix("get").toLowerCase()

                logger.info("Registering resolver for $type.$fieldName via $beanName bean")

                method.isAccessible = true

                fieldName to method.call(resolver) as DataFetcher<*>
            } else {
                null
            }
        }.toMap<String, DataFetcher<*>>()

        type to fieldNameToDataFetcher

    }.toMap()


    override fun providesDataFetcher(environment: FieldWiringEnvironment): Boolean {
        val fieldType = environment.fieldType
        val fieldDefinition = environment.fieldDefinition
        val fieldName = fieldDefinition.name
        val parentTypeName = environment.parentType.name

        val fieldDataFetchers = typeToFieldDataFetcherMap[parentTypeName]
        val dataFetcher = fieldDataFetchers?.get(fieldName)


        return dataFetcher != null
    }

    override fun getDataFetcher(environment: FieldWiringEnvironment): DataFetcher<*>? {
        val fieldType = environment.fieldType
        val fieldDefinition = environment.fieldDefinition
        val fieldName = fieldDefinition.name
        val parentTypeName = environment.parentType.name

        val fieldDataFetchers = typeToFieldDataFetcherMap[parentTypeName]
        val dataFetcher = fieldDataFetchers?.get(fieldName)

        logger.info("Resolver provided for $parentTypeName.$fieldName")

        return dataFetcher
    }

}