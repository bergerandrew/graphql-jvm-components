package me.andrewberger.graphql.federation

import com.apollographql.federation.graphqljava.SchemaTransformer
import com.apollographql.federation.graphqljava._Entity
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import graphql.schema.TypeResolver
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

//Requirements:
//  Resolver/DataFetcher
//  Type that should call resolver
//  Resolvers return type
//See https://github.com/apollographql/apollo-server/blob/570f548b88750a06fbf5f67a4abe78fb0f870ccd/packages/apollo-federation/src/types.ts#L72-L108

/**
 * Add ability to resolve references for types that are entities.
 * @param classToTypeNameMap Map of classes to the __typename they represent.  Entries for reference resolvers that return maps (which include __typename) are not needed.
 * @param typeNameToDataFetcherMap Map of __typename to the reference resolver which fetches instances of the entity
 * @param schemaTransformerCustomizer schemaTransformerCustomizer Override configuration of the SchemaTransformer before building GraphQLSchema.
 */
class FederationTransformerBuilder(classToTypeNameMap: Map<KClass<*>, String> = mapOf(), typeNameToDataFetcherMap: Map<String, EntityDataFetcher<*>> = mapOf(),
                                   private var schemaTransformerCustomizer: SchemaTransformerCustomizer = NoOpSchemaTransformer()) {

    private val classToTypeNameMutableMap = classToTypeNameMap.toMutableMap()
    private val typeNameToDataFetcherMutableMap = typeNameToDataFetcherMap.toMutableMap()

    /**
     * Add additional entity configuration by providing EntityWiring objects.  Overwrites any existing configuration for the provided value.
     */
    fun entity(entity: EntityWiring) = apply {
        typeNameToDataFetcherMutableMap[entity.typeName] = entity.resolver
        classToTypeNameMutableMap[entity.resolvedType.kotlin] = entity.typeName
    }

    /**
     * Add additional entity configuration by providing EntityWiring objects.  Overwrites any existing configuration for the provided value.
     */
    fun entity(entity: MapEntityWiring) = apply {
        typeNameToDataFetcherMutableMap[entity.typeName] = entity.resolver
    }

    /**
     * Override configuration of the SchemaTransformer before building GraphQLSchema.
     */
    fun schemaTransformerCustomizer(schemaTransformerCustomizer: SchemaTransformerCustomizer) = apply { this.schemaTransformerCustomizer = schemaTransformerCustomizer }

    /**
     * Sets the entity DataFetcher and TypeResolve and builds a GraphQLSchema from the provided SchemaTransformer.
     */
    fun build(schemaTransformer: SchemaTransformer) = schemaTransformer
            .fetchEntities(entityDataFetcher(typeNameToDataFetcherMutableMap))
            .resolveEntityType(entityTypeResolver(classToTypeNameMutableMap))
            .let { schemaTransformerCustomizer.customizeSchemaTransformer(it) }
            .build()
}


fun entityTypeResolver(classToTypeNameMap: Map<KClass<*>, String>): TypeResolver {
    return TypeResolver { typeResolutionEnvironment ->
        val objectInUnion = typeResolutionEnvironment.getObject<Any>()

        val typeDefinitionName = if (objectInUnion is Map<*, *> && objectInUnion["__typename"] is String) {
            objectInUnion["__typename"] as String
        } else {
            classToTypeNameMap[objectInUnion::class]
        }

        typeResolutionEnvironment.schema.getObjectType(typeDefinitionName)
    }
}


fun entityDataFetcher(typeNameToDataFetcherMap: Map<String, EntityDataFetcher<*>>): DataFetcher<CompletableFuture<List<Any?>?>> {
    return DataFetcher<CompletableFuture<List<Any?>?>> { dataFetchingEnvironment ->
        val representations = dataFetchingEnvironment.getArgument<List<Map<String, Any>>>(_Entity.argumentName);

        val completableFutures = representations.map { representation ->
            val typeName: String = representation["__typename"] as String

            val type = dataFetchingEnvironment.graphQLSchema.getType(typeName)

            if (type == null || type !is GraphQLObjectType) {
                throw Exception("""The _entities resolver tried to load an entity for type "$typeName", but no object type of that name was found in the schema""")
            }

            //get the entityResolver for the __typeName
            val typeReferenceDataFetcher = typeNameToDataFetcherMap[typeName]
            val resultEntity = typeReferenceDataFetcher?.resolveReference(representation, dataFetchingEnvironment) ?: representation
            if (resultEntity !is CompletableFuture<*>) {
                CompletableFuture.completedFuture(resultEntity)
            } else {
                resultEntity
            }
        }

        val joinedCompletableFuture = CompletableFuture.allOf(*completableFutures.toTypedArray()).thenApply { completableFutures.map { it?.join() } }
        joinedCompletableFuture
    }
}
