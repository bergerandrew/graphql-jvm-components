package me.andrewberger.graphql.apq

import graphql.ExecutionInput
import graphql.GraphQL
import graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport
import graphql.execution.preparsed.persisted.PersistedQuerySupport.PERSISTED_QUERY_MARKER

import graphql.schema.DataFetcher
import graphql.schema.idl.*

val QUERY_TYPENAME: String = "{__typename}"
val QUERY_TYPNEAME_HASH: String = "ecf4edb46db40b5132295c0291d62fb65d6759a9eedfa4d5d612dd5ec54a6b38"

fun main() {

    val optimisticQuery: String? = null
    val extensions = mapOf("persistedQuery" to mapOf("version" to 1, "sha256Hash" to QUERY_TYPNEAME_HASH))
    val variable = emptyMap<String, Any>()


    val graphQL = Example().graphQL()
    val executionInput = Example().buildExecution(optimisticQuery,extensions )

    val optimisticExecutionResult = graphQL.execute(executionInput)
    optimisticExecutionResult


    val normalExecutionResult = graphQL.execute(Example().buildExecution(QUERY_TYPENAME, extensions ))
    normalExecutionResult

    val secondOptimisticExecutionResult = graphQL.execute(executionInput)
    secondOptimisticExecutionResult
}

class Example {

    fun graphQL(): GraphQL {

        val schemaParser = SchemaParser()

        val fooDataFetcher: DataFetcher<String> = DataFetcher<String> {
            "Hello"
        }

        val typeDefinitionRegistry = SchemaParser().parse(
            """
            schema {
                query: QueryType
            }

            type QueryType {
                greeting: String
            }
        """.trimIndent()
        )

        val dataFetcher = DataFetcher { "Hello World" }

        val runtimeWiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
            .type(TypeRuntimeWiring.newTypeWiring("QueryType").dataFetcher("greeting", dataFetcher))
            .build()

        val graphQLSchema = SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

        return GraphQL.newGraphQL(graphQLSchema).preparsedDocumentProvider(ApolloPersistedQuerySupport(BasicApolloAutomaticPersistedQueryCache())).build()
    }

    fun buildExecution(query: String?, extensions: Map<String, Any>, vararg varPairs: Pair<String, Any>): ExecutionInput {
        return ExecutionInput.Builder()
            .apply {
                if (query == null && extensions.containsKey("persistedQuery")) {
                    query(PERSISTED_QUERY_MARKER)
                } else {
                    query(query)
                }
            }
            .variables(varPairs.toMap())
            .extensions(extensions)
            .build()
    }

}


