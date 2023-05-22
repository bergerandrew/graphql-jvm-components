package me.andrewberger.graphql.exception.handler

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ExceptionHandlerWrapperTest {


    @Test
    fun wrapperTest() {
        val mockDataFetcherExceptionHandler = Mockito.mock(DataFetcherExceptionHandler::class.java)
        val wrappedMockDataFetcherExceptionHandler = wrap(mockDataFetcherExceptionHandler) {
            it.exception
        }
        val mockDataFetcherExceptionHandlerParameters = Mockito.mock(DataFetcherExceptionHandlerParameters::class.java)
        wrappedMockDataFetcherExceptionHandler.onException(mockDataFetcherExceptionHandlerParameters)
        Mockito.verify(mockDataFetcherExceptionHandlerParameters).exception
        Mockito.verify(mockDataFetcherExceptionHandler).onException(mockDataFetcherExceptionHandlerParameters)
    }

}