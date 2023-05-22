package me.andrewberger.graphql.exception.handler;

import graphql.execution.DataFetcherExceptionHandlerParameters;

public interface ExceptionHandlerWrapperCallBack {
    void cb(DataFetcherExceptionHandlerParameters dataFetcherExceptionHandlerParameter);
}
