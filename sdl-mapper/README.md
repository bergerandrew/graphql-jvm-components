# sdl-mapper (name TBD)
SDL first GraphQL framework for Java/Kotlin built on top of [graphql-java](https://github.com/graphql-java/graphql-java) and [graphql-java-spring](https://github.com/graphql-java/graphql-java-spring).  
While many components of graph-java-components can work independently of this framework and integrate into other frameworks or be used independently, this framework provides defaults and integrations for 
Spring Boot applications. 

#### Features and Defaults:
* `/graphql` controller with `POST` and `GET` support via [graphql-java-spring](https://github.com/graphql-java/graphql-java-spring)
* `Production` Spring Profile disables introspection queries. 
* Schema parsing and merging from `classpath:/resources/schema` via [`SchemaLoader`](graphql-java-sdl-mapper/src/main/kotlin/me/andrewberger/graphql/config/schemaloader/DefaultSchemaLoader.kt)
* Federated Tracing instrumentation
* Federation _Entities_ via `FederatedEntity` interface and `DataFetchersResolvedType` annotation
* Resolver support via `DataFetchers` interface with `@DataFetchersFor` annotation and field level functions with `@FieldDataFetcher`
* Union and Interface TypeResolvers via `@DataFetchersResolvedType` annotation
* Per-request DataLoaders from `BatchLoader` interface
* Programmatic DataLoader access (kotlin: `dataFetchingEnvironment.dataLoaderOf<KeyType, LoadedType>()`)
* Automatic wiring of `WiringFactory` beans
* FieldVisibilityProvider
* AuthDirectiveSupport
* PreparsedDocumentProvider
* SchemaDirectiveWiringProvider
* WiringFactoryProvider

