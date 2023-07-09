@file:JvmName("ExceptionHandlerWrapper")

package me.andrewberger.graphql.exception.handler

import graphql.execution.DataFetcherExceptionHandler

/**
 * Wraps a [DataFetcherExceptionHandler] to allow for calling a [ExceptionHandlerWrapperCallBack] first
 * @param dataFetcherExceptionHandler The handler to wrap
 * @param exceptionHandlerWrapperCallBack The callback to invoke before executing the original handler
 * @return A [DataFetcherExceptionHandler] wrapping the original dataFetcherExceptionHandler
 */
fun wrap(dataFetcherExceptionHandler: DataFetcherExceptionHandler, exceptionHandlerWrapperCallBack: ExceptionHandlerWrapperCallBack) =
        DataFetcherExceptionHandler {
            exceptionHandlerWrapperCallBack.cb(it)
            dataFetcherExceptionHandler.onException(it)
        }