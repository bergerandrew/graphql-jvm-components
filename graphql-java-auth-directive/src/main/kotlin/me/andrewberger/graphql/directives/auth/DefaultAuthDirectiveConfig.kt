package me.andrewberger.graphql.directives.auth

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.SchemaDirectiveWiringEnvironment


open class DefaultAuthDirectiveConfig: AuthDirectiveConfig {
    @Throws(Exception::class)
    override fun dataFetcherAuthWrapper(originalDataFetcher: DataFetcher<*>, wrapperDataFetchingEnvironment: DataFetchingEnvironment, environment: SchemaDirectiveWiringEnvironment<*>): Any {
//        originalDataFetcher.get(wrapperDataFetchingEnvironment)
        throw Exception("Not Authorized")
    }
}