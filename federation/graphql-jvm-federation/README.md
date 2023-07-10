# graphql-jvm-federation
Provides programmatic support for resolving entities.
Apollo's federation support provides a `SchemaTransformer` by calling `Federation.transform(...)`  but does not configure the transformer.  
This project aims to make it easily configurable.  


### Example:
```graphql
schema {
    query: QueryType
}

type QueryType {
    ingredient(name: String): Ingredient!
}

type Ingredient @key(fields: "name"){
    name: String!
}
```
Use the `FederationTransformerBuilder` to specify entities by providing `EntityWiring` instances.  
`EntityWiring` takes several parameters.  
* `typeName: String` or `typeDefinition: TypeDefinition` - The name of the type to configure a resolver for.  This type should already be 
marked as an entity via the schema transformer (for instance by having a `@key` directive in SDL).  
* `resolver: EntityDataFetcher` - The dataFetcher/resolver to use to resolve instances of this entity.  
* `resolvedType: Class`  - The object type that is returned by the resolver.  This is used internally for mapping the return type of the 
entityDataFetcher to a `TypeResolver` which provides the concrete type of the entity.  

`FederationTransformerBuilder` can also take a map of classes to type names and a map of type names to `EntityDataFetcher` in its constructor.  
```kotlin
        //Plain basic graphql-java wiring
        val runtimeWiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("QueryType")
                        .dataFetcher("ingredient", IngredientDataFetcher().getIngredient())
                )
                .build()

        //Apollo transformer to add in entity support
        val schemaTransformer = Federation.transform(typeRegistry, runtimeWiring)

        //NEW: improve federation support by allowing resolvers/datafetchers for each individual entity to be passed in at once
        val federationTransformerBuilder = FederationTransformerBuilder()
                .entity(EntityWiring("Ingredient", IngredientEntityResolver(), Ingredient::class))

        //NEW: Pass the Apollo SchemaTransformer to the federationTransformerBuilder
        val graphQLSchema = federationTransformerBuilder.build(schemaTransformer)

        //Plain graphql-java
        val graphQL: GraphQL = GraphQL.newGraphQL(graphQLSchema).build()
```
```kotlin
data class Ingredient(val name: String)
```
Implement the `EntityDataFetcher`  and override `resolveReference` to define how to resolve the type as an entity.  
This is similar to the behavior of the [`__resolveReference`](https://www.apollographql.com/docs/federation/api/apollo-federation/#__resolvereference) 
field in JS.  
```kotlin
class IngredientEntityResolver: EntityDataFetcher<CompletableFuture<Ingredient>?> {
    override fun resolveReference(args: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<Ingredient>? {
        val ingredientName = args["name"] as String
        val ingredientDataLoader = dataFetchingEnvironment.getDataLoader<String, Ingredient>("example.ingredient")
        return ingredientDataLoader.load(ingredientName)
    }
}
```
See [full usage example](../graphql-jvm-federation-example)

