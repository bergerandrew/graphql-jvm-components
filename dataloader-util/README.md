# dataloader-util
Programmatic pattern for registering and loading dataloaders.


#### How to use
The `DataLoaderRegistryFactory` will produce a `DataLoaderRegistry` for the given `DataLoaderWiring`.  
```kotlin
val loaderKeyType: KClass<String> = String::class
val loaderReturnType: KClass<Food> = Food::class
val foodBatchLoader: BatchLoader<String, Food> = BatchLoader<String, Food> { keys: List<String> ->
    CompletableFuture.supplyAsync { foodService.foodsById(keys) }
}
val foodDataLoaderWiring = DataLoaderWiring(loaderKeyType, loaderReturnType, foodBatchLoader)

val dataLoaderRegistry = DataLoaderRegistryFactory.dataLoaderRegistry(lisdtOf(foodDataLoaderWiring))
```
`DataFetcher`s can the access the `DataLoader` by requesting it using the known key and return type from the `DataFetchingEnvironment`
```kotlin
DataFetcher { dataFetchingEnvironment ->
            val foodDataLoader = it.dataLoaderOf<String, Food>()!!
            foodDataLoader.load(dataFetchingEnvironment.getArgument("name"))
}
```

Register the `DataLoaderRegistry`
```kotlin
executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry)
```

A `DataLoaderDispatcherInstrumentationOptions` is responsible for dispatching "all the data loaders as each level of the graphql query is executed and hence make batched objects available to the query and the associated DataFetchers".  
It accepts a DataLoaderDispatcherInstrumentationOptions object that can be set to track batching efficiency statistics.
```kotlin
fun dispatcherInstrumentation() : Instrumentation {
    val options: DataLoaderDispatcherInstrumentationOptions = DataLoaderDispatcherInstrumentationOptions
            .newOptions().includeStatistics(true)

    return DataLoaderDispatcherInstrumentation(options)
}
```
The DataLoaderDispatcherInstrumentation is provided by GraphQL-Java by default if one is not provided. 
