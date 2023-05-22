package me.andrewberger.graphql.federation.example

import com.apollographql.federation.graphqljava.Federation
import me.andrewberger.graphql.federation.EntityDataFetcher
import me.andrewberger.graphql.federation.EntityWiring
import me.andrewberger.graphql.federation.FederationTransformerBuilder
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver


fun main() {

    val query: String = """
        query(${'$'}_representations: [_Any!]!) {
            ingredient(name: "Cheese") {
                name
            }
            _entities(representations: ${'$'}_representations) {
                __typename
                ... on Ingredient {
                    name
                }
            }
        }
    """.trimIndent()
    val variable = "_representations" to listOf(
            mapOf<String, Any>(
                    "__typename" to "Ingredient",
                    "name" to "Tomato"
            )
    )


    val graphQL = Example().graphQL()
    val executionInput = Example().buildExecution(query, variable)

    val executionResult = graphQL.execute(executionInput)
    executionResult
}

class Example {

    fun graphQL(): GraphQL {

        val cl = this.javaClass.classLoader
        val resolver: ResourcePatternResolver = PathMatchingResourcePatternResolver(cl)
        val graphqlsFiles = resolver.getResources("${ResourceLoader.CLASSPATH_URL_PREFIX}/schema/*.graphqls").asList()
                .filter { it.isReadable }
                .map { it.inputStream.reader() }

        val schemaParser = SchemaParser()

        //creating and merging multiple TypeDefinitionRegistries so that schema can be modularized
        val fileTypeDefinitionRegistries = graphqlsFiles.map(schemaParser::parse)
        val typeRegistry: TypeDefinitionRegistry = TypeDefinitionRegistry()
                .apply { fileTypeDefinitionRegistries.forEach { merge(it) } }

        //Plain basic graphql-java tedious wiring
        val runtimeWiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("QueryType")
                        .dataFetcher("food", FoodDataFetcher().getFood())
                        .dataFetcher("ingredient", IngredientDataFetcher().getIngredient())
                )
                .type(TypeRuntimeWiring.newTypeWiring("Food")
                        .dataFetcher("ingredient", IngredientDataFetcher().getIngredient())
                )
                .build()

        //Apollo transformer to wire in entity support
        val schemaTransformer = Federation.transform(typeRegistry, runtimeWiring)

        //NEW: improve federation support by allowing resolvers/datafetchers for each individual entity to be passed in at once
        val federationTransformerBuilder = FederationTransformerBuilder()
//                .entity(Entity(typeDefinition = typeRegistry.getType("Foo").get(), resolver = FooEntityDataFetcher(), resolvedType = Foo::class))
//                .entity(Entity("Food", foodResolver, Food::class))
                .entity(EntityWiring("Ingredient", IngredientEntityResolver(), Ingredient::class.java))

        //New: Pass the Apollo SchemaTransformer to the federationTransformerBuilder
        val graphQLSchema = federationTransformerBuilder.build(schemaTransformer)

        //Plain graphql-java
        return GraphQL.newGraphQL(graphQLSchema).build()
    }

    fun buildExecution(query: String, vararg varPairs: Pair<String, Any>): ExecutionInput {
        return ExecutionInput.Builder()
                .query(query)
                .variables(varPairs.toMap())
                .dataLoaderRegistry(DataLoaderRegistry().apply {
                    register("example.food", DataLoader.newDataLoader(FoodBatchLoader()))
                    register("example.ingredient", DataLoader.newDataLoader(IngredientBatchLoader()))
                })
                .build()
    }

}


