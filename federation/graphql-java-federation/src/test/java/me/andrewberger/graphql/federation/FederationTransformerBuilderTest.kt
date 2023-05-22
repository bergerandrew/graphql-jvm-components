package me.andrewberger.graphql.federation

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava._Entity
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironmentImpl
import graphql.schema.GraphQLSchema
import graphql.schema.SchemaTransformer
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

data class SomeType(val name: String)

class FederationTransformerBuilderTest {

    @Test
    fun entityTypeResolverSuccessTest() {
        val someTypeName = "Food"
        val someType = SomeType("Pizza")
        val classToTypeNameMap = mapOf<KClass<*>, String>(someType::class to someTypeName)
        val entityTypeResolver = entityTypeResolver(classToTypeNameMap)

        val runtimeWiring = getRuntimeWiring(someTypeName, DataFetcher { someType })
        val typeDefinitionRegistry = getTypeDefinitionRegistry(someTypeName)

        val graphQLSchema: GraphQLSchema = Federation.transform(typeDefinitionRegistry, runtimeWiring)
                .fetchEntities { someType }
                .resolveEntityType(entityTypeResolver)
                .build()

        val mockTypeResolutionEnvironment = TypeResolutionEnvironment(someType, emptyMap(), null, null, graphQLSchema, null)
        val expectedObjectType = graphQLSchema.getObjectType(someTypeName)
        val resultObjectType = entityTypeResolver.getType(mockTypeResolutionEnvironment)
        assertEquals(expectedObjectType, resultObjectType)
    }

    @Test
    fun entityTypeResolverRepresentationFallbackTest() {
        val someTypeName = "Food"
        val someType = mapOf<String, Any>(
                "__typename" to someTypeName,
                "name" to "Pizza"
        )
        val classToTypeNameMap = emptyMap<KClass<*>, String>()
        val entityTypeResolver = entityTypeResolver(classToTypeNameMap)

        val runtimeWiring = getRuntimeWiring(someTypeName, DataFetcher { someType })
        val typeDefinitionRegistry = getTypeDefinitionRegistry(someTypeName)

        val graphQLSchema: GraphQLSchema = Federation.transform(typeDefinitionRegistry, runtimeWiring)
                .fetchEntities {  }
                .resolveEntityType(entityTypeResolver)
                .build()

        val mockTypeResolutionEnvironment = TypeResolutionEnvironment(someType, emptyMap(), null, null, graphQLSchema, null)
        val expectedObjectType = graphQLSchema.getObjectType(someTypeName)
        val resultObjectType = entityTypeResolver.getType(mockTypeResolutionEnvironment)
        assertEquals(expectedObjectType, resultObjectType)
    }


    @Test
    fun entityTypeResolverFailTest() {
        val someTypeName = "Food"
        val someType = SomeType("Pizza")
        val classToTypeNameMap = mapOf<KClass<*>, String>(someType::class to someTypeName)
        val entityTypeResolver = entityTypeResolver(classToTypeNameMap)

        val runtimeWiring = getRuntimeWiring(someTypeName, DataFetcher { someType })
        val typeDefinitionRegistry = getTypeDefinitionRegistry(someTypeName)

        val graphQLSchema: GraphQLSchema = Federation.transform(typeDefinitionRegistry, runtimeWiring)
                .fetchEntities { someType }
                .resolveEntityType(entityTypeResolver)
                .build()

        val mockTypeResolutionEnvironment = TypeResolutionEnvironment(someType, emptyMap(), null, null, graphQLSchema, null)
        val expectedObjectType = graphQLSchema.getObjectType("Vehicle")
        val resultObjectType = entityTypeResolver.getType(mockTypeResolutionEnvironment)
        assertNotEquals(expectedObjectType, resultObjectType)
    }

    @Test
    fun entityDataFetcherSuccessTest() {
        val someTypeName = "Food"
        val someType = SomeType("Pizza")
        val someDataFetcher = buildEntityDataFetcher { args, dataFetchingEnvironment -> if (args["__typename"] == someTypeName && args["name"] == "Pizza") someType else null }
        val typeNameToDataFetcherMap = mapOf(someTypeName to someDataFetcher)
        val entityDataFetcher = entityDataFetcher(typeNameToDataFetcherMap)
        val arguments = mapOf<String, List<Any>>(
                _Entity.argumentName to listOf(
                        mapOf<String, Any>(
                                "__typename" to someTypeName,
                                "name" to "Pizza"
                        )
                )
        )

        val schema = Federation.transform(getTypeDefinitionRegistry(someTypeName), getRuntimeWiring(someTypeName, DataFetcher { }))
                .fetchEntities { someType }
                .resolveEntityType( TypeResolver { null })
                .build()

        val dataFetchingEnvironment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .arguments(arguments)
                .graphQLSchema(schema)
                .build()
        val resultSomeType = entityDataFetcher.get(dataFetchingEnvironment).get(1, TimeUnit.MILLISECONDS)!!.single()
        assertEquals(someType, resultSomeType)
    }

    @Test
    fun entityDataFetcherRepresentationFallbackTest() {
        val someTypeName = "Food"
        val entityDataFetcher = entityDataFetcher(emptyMap())
        val arguments = mapOf<String, List<Any>>(
                _Entity.argumentName to listOf(
                        mapOf<String, Any>(
                                "__typename" to someTypeName,
                                "name" to "Pizza"
                        )
                )
        )

        val schema = Federation.transform(getTypeDefinitionRegistry(someTypeName), getRuntimeWiring(someTypeName, DataFetcher { }))
                .fetchEntities {  }
                .resolveEntityType( TypeResolver { null })
                .build()

        val dataFetchingEnvironment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .arguments(arguments)
                .graphQLSchema(schema)
                .build()
        val resultSomeType = entityDataFetcher.get(dataFetchingEnvironment).get(1, TimeUnit.MILLISECONDS)!!.single()
        assertEquals(mapOf("__typename" to someTypeName, "name" to "Pizza"), resultSomeType)
    }

    @Test
    fun entityDataFetcherFailTest() {
        val someTypeName = "Food"
        val someType = SomeType("Pizza")
        val someDataFetcher = buildEntityDataFetcher { args, dataFetchingEnvironment -> if (args["__typename"] == someTypeName && args["name"] == "Pizza") someType else null }
        val typeNameToDataFetcherMap = mapOf(someTypeName to someDataFetcher)
        val entityDataFetcher = entityDataFetcher(typeNameToDataFetcherMap)
        val arguments = mapOf<String, List<Any>>(
                _Entity.argumentName to listOf(
                        mapOf<String, Any>(
                                "__typename" to "Vehicle",
                                "name" to "Pizza"
                        )
                )
        )

        val schema = Federation.transform(getTypeDefinitionRegistry(someTypeName), getRuntimeWiring(someTypeName, DataFetcher { }))
                .fetchEntities { someType }
                .resolveEntityType( TypeResolver { null })
                .build()

        val dataFetchingEnvironment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .arguments(arguments)
                .graphQLSchema(schema)
                .build()
        val throwable = assertThrows<Exception> {
            entityDataFetcher.get(dataFetchingEnvironment).get(1, TimeUnit.MILLISECONDS)!!.single()
        }
        assertEquals("The _entities resolver tried to load an entity for type \"Vehicle\", but no object type of that name was found in the schema", throwable.message)
    }

    private fun getTypeDefinitionRegistry(someTypeName: String) = SchemaParser().parse("""
            schema { query: QueryType }

            type QueryType {
                someType(name: String): $someTypeName!
            }

            type $someTypeName @key(fields: "name") { name: String! }
        """)

    private fun getRuntimeWiring(someTypeName: String, dataFetcher: DataFetcher<*>): RuntimeWiring {
        val someTypeWiring = TypeRuntimeWiring.newTypeWiring(someTypeName) {
            it.apply {
                dataFetcher("name", dataFetcher)
            }
        }
        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(someTypeWiring)
                .build()
        return runtimeWiring
    }
}

