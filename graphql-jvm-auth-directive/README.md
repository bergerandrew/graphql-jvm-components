# graphql-jvm-auth-directive
Add capabilities to a service for including an `@auth` directive in SDL to protect resources.  

Built following the suggestions of:  
https://www.graphql-java.com/documentation/v15/sdl-directives/  
https://www.apollographql.com/docs/apollo-server/schema/creating-directives/#enforcing-access-permissions  

### Example Usage:
Implement the `AuthDirectiveConfig` interface.  
This interface has two methods:  
* `hasAuthDirective` - Determines if the SchemaDirectiveWiringEnvironment is eligible for auth. 
Note that this can be any part of a schema representable by a `GraphQLDirectiveContainer`.  Defaults to an `@auth` directive.  
* `dataFetcherAuthWrapper` - The behavior to apply when attempting to resolve the schema wrapped by the chosen directive.  The original behavior can be executed by calling and returning 
`originalDataFetcher.get(wrapperDataFetchingEnvironment)`.  
The `SchemaDirectiveWiringEnvironment` is made available to provide access to context such as arguments applied to the directive ex. `@auth(scope: Admin)`

```kotlin
class MyAuthDirectiveConfig: AuthDirectiveConfig {
    //Determines if the SchemaDirectiveWiringEnvironment has the required directive to apply auth
    override fun hasAuthDirective(environment: SchemaDirectiveWiringEnvironment<*>): Boolean  {
            return environment.containsDirective("auth")
        }

    override fun dataFetcherAuthWrapper(originalDataFetcher: DataFetcher<*>, wrapperDataFetchingEnvironment: DataFetchingEnvironment, environment: SchemaDirectiveWiringEnvironment<*>): Any {
//        originalDataFetcher.get(wrapperDataFetchingEnvironment)
        throw Exception("Not Authorized")
    }
}
```
Add a `WiringFactory` and register it.  
An `AuthDirectiveWiringFactory` is already provided:  
```kotlin
val wiringFactory: WiringFactory = AuthDirectiveWiringFactory(MyAuthDirectiveConfig())
```

```kotlin
    RuntimeWiring.newRuntimeWiring()
                 .wiringFactory(wiringFactory)
                 .build()
```
or
```kotlin
    RuntimeWiring.newRuntimeWiring()
                 .directiveWiring(AuthDirectiveWiring(MyAuthDirectiveConfig()))
                 .build()
```
