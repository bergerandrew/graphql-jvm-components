# graphql-jvm-components
A collection of components for the JVM to simplify building GraphQL services.  

## Components

### [sdl-mapper](sdl-mapper)
Spring Boot framework for graph-java utilizing and supporting many of the components in this repo.  

### [Print Schema Plugin](printschema-nocode-maven-plugin)
Maven build plugin that prints a `schema.graphqls` of a project's schema.

---

### [Federation](federation)
Components for adding federation capabilities like entities and federated tracing.  Built on top of [apollographql/federation-jvm](https://github.com/apollographql/federation-jvm).
#### [graphql-jvm-federation](federation/graphql-jvm-federation)
Base wrapper for entity support.

---

### [automatic-persisted-queries-preparsed-document-provider](automatic-persisted-queries-preparsed-document-provider)
Adds [Automatic Persisted Query](https://www.apollographql.com/docs/apollo-server/performance/apq/) caching to GraphQL-Java 16 and up.

### [graphql-jvm-auth-directive](graphql-jvm-auth-directive)
Add capabilities to a service for including an `@auth` directive in SDL to protect resources.  

### [caffeine-preparsed-document-provider](caffeine-preparsed-document-provider)
Config to add a PreparsedDocumentProvider that caches results.  This avoids the need to parse and revalidate queries.  See the [GraphQL-java execution documentation](https://www.graphql-java.com/documentation/v14/execution/) Query Caching section for details.
 
### [dataloader-util](dataloader-util)
Programmatic pattern for registering and loading dataloaders.

### [datafetching-exception-handler](datafetching-exception-handler)
Override graphQL-java's default exception handler by passing the handler to an execution strategy.

---
