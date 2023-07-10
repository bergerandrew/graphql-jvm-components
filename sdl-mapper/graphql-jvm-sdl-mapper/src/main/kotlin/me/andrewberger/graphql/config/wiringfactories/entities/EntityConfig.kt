package me.andrewberger.graphql.config.wiringfactories.entities

import me.andrewberger.graphql.annotations.DataFetchersFor
import me.andrewberger.graphql.annotations.DataFetchersResolvedType
import me.andrewberger.graphql.dataFetchers.FederatedEntity
import me.andrewberger.graphql.federation.FederationTransformerBuilder
import me.andrewberger.graphql.federation.buildEntityDataFetcher

class EntityConfig(federatedEntityResolvers: Map<String, FederatedEntity<*>>): EntityConfigurationProvider {
    private val typeToDataFetcherMap = federatedEntityResolvers.filter { it.value.javaClass.isAnnotationPresent(DataFetchersFor::class.java) }
            .map { (beanName, dataFetcher) ->
                val dataFetcherComponent = dataFetcher.javaClass.getAnnotation(DataFetchersFor::class.java)
                val typeDefinitionName = dataFetcherComponent.schemaType

                typeDefinitionName to dataFetcher
            }.toMap()

    private val classToTypeMap = federatedEntityResolvers.map { (beanName, entityResolver) ->
//        val type = entityResolver::class.members.find { it.name == entityResolver::resolveReference.name }!!.returnType
        val dataFetcherComponent = entityResolver.javaClass.getAnnotation(DataFetchersFor::class.java)
        val typeDefinitionName = dataFetcherComponent.schemaType
        val resolvedTypeAnnotation = entityResolver.javaClass.getAnnotation(DataFetchersResolvedType::class.java)
        val schemaTypeClass = resolvedTypeAnnotation.schemaTypeClass

        schemaTypeClass to typeDefinitionName
    }.toMap()

    override fun entityConfiguration(): FederationTransformerBuilder {
        return FederationTransformerBuilder(classToTypeMap, typeToDataFetcherMap.mapValues { (_, federatedEntity) ->
            buildEntityDataFetcher { args, dataFetchingEnvironment -> federatedEntity.resolveReference(args, dataFetchingEnvironment) }
        })
    }

}