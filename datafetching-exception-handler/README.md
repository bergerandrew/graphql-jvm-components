# datafetching-exception-handler

Override graphQL-java's default exception handler by passing a handler to an execution strategy.  

```kotlin
GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(AsyncExecutionStrategy(dataFetchingExceptionHandler))
                .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetchingExceptionHandler))
                .build()
```

`ProductionDataFetcherExceptionHandler` replaces all exceptions with the message `"Internal Server Error"` 
unless the exception is of type `GraphQLError`:
```kotlin
GraphqlErrorException.Builder().message("Unauthorized").build()
```

Custom logic can be added by directly using `ExceptionHandlerWrapper.wrap(orignalDataFetchingExceptionHandler, wrapper)` and implementing an `ExceptionHandlerWrapperCallBack`.  
Or simply implement a new ExceptionHandler to change its behavior.  
