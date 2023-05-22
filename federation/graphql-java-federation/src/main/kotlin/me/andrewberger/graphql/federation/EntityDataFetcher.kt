package me.andrewberger.graphql.federation

import graphql.schema.DataFetchingEnvironment

//Wrapper for the entity resolver logic for a specific entity
interface EntityDataFetcher<T> {
    fun resolveReference(args: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment): T
}

fun <T> buildEntityDataFetcher(resolveReference: (args: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment) -> T) = object : EntityDataFetcher<T> {
    override fun resolveReference(args: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment) = resolveReference(args, dataFetchingEnvironment)
}