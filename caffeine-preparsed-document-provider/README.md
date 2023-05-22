# caffeine-preparsed-document-provider
Uses [Caffeine](https://github.com/ben-manes/caffeine) to cache query parsing and validation.  
This is an implementation of the PreparsedDocumentProvider example described in the `Query Caching` section of the [graphql-java execution documentation](https://www.graphql-java.com/documentation/v14/execution/).


### Usage
```java
    GraphQL graphQL = GraphQL.newGraphQL(schema)
            .preparsedDocumentProvider(CaffeinePreparsedDocumentProvider(caffeineCache))
            .build();
```

### Note
Inline field arguments in a query cause them to be considered unique operations and will reduce cache hits.  Passing field arguments as variables will improve cache performance.  
#### Example optimization from graphql-java docs:
The following query:
```graphql
    query HelloTo {
         sayHello(to: "Me") {
            greeting
         }
    }
```
Should be rewritten as: 
```graphql
    query HelloTo($to: String!) {
         sayHello(to: $to) {
            greeting
         }
    }
```
with variables:
```json
    {
       "to": "Me"
    }
```
The query is now reused regardless of variable values provided.
