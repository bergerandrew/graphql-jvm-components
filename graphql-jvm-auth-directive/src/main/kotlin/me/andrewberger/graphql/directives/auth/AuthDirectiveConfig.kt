package me.andrewberger.graphql.directives.auth

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

interface AuthDirectiveConfig {
    fun hasAuthDirective(environment: SchemaDirectiveWiringEnvironment<*>): Boolean  {
        return environment.containsDirective("auth")
    }

    @Throws(Exception::class)
    fun dataFetcherAuthWrapper(originalDataFetcher: DataFetcher<*>, wrapperDataFetchingEnvironment: DataFetchingEnvironment, environment: SchemaDirectiveWiringEnvironment<*>): Any {
        return originalDataFetcher.get(wrapperDataFetchingEnvironment)
    }
}