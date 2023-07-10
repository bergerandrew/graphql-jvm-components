# automatic-persisted-queries-preparsed-document-provider

Adds [Automatic Persisted Query](https://www.apollographql.com/docs/apollo-server/performance/apq/) caching to GraphQL-Java 16 and up.

### How it works:
Uses GraphQL-Java provided `ApolloPersistedQuerySupport` class as a starting point.

**Note** that GraphQL-Java will need to pass extensions into the execution input and mark empty queries with `graphql.execution.preparsed.persisted.PersistedQuerySupport.PERSISTED_QUERY_MARKER` (See [Example.kt](src/test/kotlin/me/andrewberger/graphql/apq/Example.kt))


automatic-persisted-queries-preparsed-document-provider provides an `ApolloAPQCache` and `ApolloAPQCacheRepository` 
as a means to write and retrieve values from a cache.  A basic `BasicApolloAPQCache` is provided which utilizes a 
`BasicApolloAPQCacheRepository` to store values in a map.

### Basic Usage:
```kotlin
GraphQL.newGraphQL(graphQLSchema)
   ...
   .preparsedDocumentProvider(ApolloPersistedQuerySupport(BasicApolloAPQCache()))
   .build()
```

### Advanced Usage:
Implement: `ApolloAPQCacheRepository`
```kotlin
class MyApolloAPQCacheRepository<K, V> : ApolloAPQCacheRepository<K, V> {
    private val map: MutableMap<K, V> = mutableMapOf()
    override fun compute(key: K, remappingFunction: BiFunction<in K?, in V?, out V?>): V? {
        //get the old value and call remappingFunction.apply(key, oldValue) to get the new value
        //then set the new value
        return map.compute(key, remappingFunction)
    }
}
```
Provide the `ApolloAPQCacheRepository` implementation to build an `ApolloAPQCache` and use to build an `ApolloPersistedQuerySupport`.
```kotlin
val cacheRepository: MyApolloAPQCacheRepository = MyApolloAPQCacheRepository()
val apolloAPQCache: ApolloAPQCache = ApolloAPQCache(cacheRepository)

GraphQL.newGraphQL(graphQLSchema)
   ...
   .preparsedDocumentProvider(ApolloPersistedQuerySupport(apolloAPQCache))
   .build()
```