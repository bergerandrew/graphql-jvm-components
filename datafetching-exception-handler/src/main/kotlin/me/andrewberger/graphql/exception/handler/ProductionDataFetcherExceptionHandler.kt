package me.andrewberger.graphql.exception.handler

import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.GraphqlErrorException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.util.LogKit

/**
 * Replaces all error messages with the message "Internal Server Error" unless the exception is of type [graphql.GraphQLError]
 */
class ProductionDataFetcherExceptionHandler: DataFetcherExceptionHandler {

    private val logNotSafe = LogKit.getNotPrivacySafeLogger(ProductionDataFetcherExceptionHandler::class.java)

    override fun onException(dataFetcherExceptionHandlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = dataFetcherExceptionHandlerParameters.exception
        val path = dataFetcherExceptionHandlerParameters.path
        val sourceLocation = dataFetcherExceptionHandlerParameters.sourceLocation
        return if (exception is GraphqlErrorException) {
            val error = ExceptionWhileDataFetching(path, exception, sourceLocation)
            logNotSafe.warn(error.message)
            DataFetcherExceptionHandlerResult.newResult().error(error).build()
        } else {
            DataFetcherExceptionHandlerResult.newResult(
                    GraphqlErrorBuilder.newError().message("Internal Server Error")
                            .location(sourceLocation)
                            .path(path)
                            .build()
                            .also { logNotSafe.warn(it.message, exception) }
            ).build()
        }

    }
}