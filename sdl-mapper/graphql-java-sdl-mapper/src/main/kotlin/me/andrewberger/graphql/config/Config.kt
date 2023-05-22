package me.andrewberger.graphql.config

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.apollographql.federation.graphqljava.tracing.HTTPRequestHeaders
import me.andrewberger.graphql.apq.BasicApolloAutomaticPersistedQueryCache
import me.andrewberger.graphql.config.dataloaders.DataLoaderRegistryProvider
import me.andrewberger.graphql.config.fieldvisilbility.DefaultFieldVisibilityProvider
import me.andrewberger.graphql.config.fieldvisilbility.FieldVisibilityProvider
import me.andrewberger.graphql.config.fieldvisilbility.NoIntrospectionFieldVisibilityProvider
import me.andrewberger.graphql.config.properties.APQProperties
import me.andrewberger.graphql.config.wiringfactories.DefaultWiringFactoryProvider
import me.andrewberger.graphql.config.wiringfactories.SchemaResolverConfig
import me.andrewberger.graphql.config.wiringfactories.WiringFactoryProvider
import me.andrewberger.graphql.config.wiringfactories.directives.DefaultSchemaDirectiveWiringProvider
import me.andrewberger.graphql.config.wiringfactories.directives.SchemaDirectiveWiringProvider
import me.andrewberger.graphql.config.schemaloader.SchemaLoader
import me.andrewberger.graphql.config.schemaloader.DefaultSchemaLoader
import me.andrewberger.graphql.config.wiringfactories.RuntimeWiringCustomizer
import me.andrewberger.graphql.directives.auth.AuthDirectiveWiringFactory
import me.andrewberger.graphql.config.wiringfactories.entities.EntityConfig
import me.andrewberger.graphql.config.wiringfactories.entities.EntityConfigurationProvider
import me.andrewberger.graphql.dataFetchers.FederatedEntity
import me.andrewberger.graphql.directives.auth.AuthDirectiveConfig
import me.andrewberger.graphql.directives.auth.DefaultAuthDirectiveConfig
import me.andrewberger.graphql.federation.FederationTransformerBuilder
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport
import graphql.execution.preparsed.persisted.PersistedQueryCache
import graphql.schema.GraphQLSchema
import graphql.schema.idl.*
import graphql.schema.visibility.GraphqlFieldVisibility
import graphql.spring.web.servlet.ExecutionInputCustomizer
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.context.request.WebRequest
import java.io.Reader
import java.util.concurrent.CompletableFuture


@EnableConfigurationProperties(APQProperties::class)
@Configuration
class Config {

    @Bean
    fun graphQl(schemaLoader: SchemaLoader, instrumentationList: List<Instrumentation>, wiringFactoryProvider: WiringFactoryProvider, schemaDirectiveWiringProvider: SchemaDirectiveWiringProvider,
                entityConfigurationProvider: EntityConfigurationProvider, fieldVisibilityProvider: FieldVisibilityProvider, preparsedDocumentProvider: PreparsedDocumentProvider,
                runtimeWiringCustomizer: RuntimeWiringCustomizer, dataFetchingExceptionHandler: DataFetcherExceptionHandler): GraphQL {
        val schema = buildSchema(schemaLoader.load(), wiringFactoryProvider.wiringFactories(), schemaDirectiveWiringProvider.schemaDirectiveWirings(), entityConfigurationProvider.entityConfiguration(), fieldVisibilityProvider.get(), runtimeWiringCustomizer)
//        validate(schema)
        return GraphQL.newGraphQL(schema)
                .instrumentation(ChainedInstrumentation(instrumentationList))
                .preparsedDocumentProvider(preparsedDocumentProvider)
                .queryExecutionStrategy(AsyncExecutionStrategy(dataFetchingExceptionHandler))
                .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetchingExceptionHandler))
                .build()
    }

    private fun buildSchema(graphqlsFiles: List<Reader>, wiringFactory: WiringFactory, directiveWirings: List<SchemaDirectiveWiring>, federationTransformerBuilder: FederationTransformerBuilder, fieldVisibility: GraphqlFieldVisibility, runtimeWiringCustomizer: RuntimeWiringCustomizer): GraphQLSchema {
        val schemaParser = SchemaParser()
        //creating and merging multiple TypeDefinitionRegistries so that schema can be modularized
        val fileTypeDefinitionRegistries = graphqlsFiles.map(schemaParser::parse)
        val typeRegistry: TypeDefinitionRegistry = TypeDefinitionRegistry()
                .apply { fileTypeDefinitionRegistries.forEach { merge(it) } }
        val runtimeWiring: RuntimeWiring = buildWiring(wiringFactory, directiveWirings, runtimeWiringCustomizer)

        //TODO: make federation support toggleable
        val schemaTransformer = Federation.transform(typeRegistry, runtimeWiring)
        val graphQLSchema = federationTransformerBuilder.build(schemaTransformer)

        return transformFieldVisibility(graphQLSchema, fieldVisibility)
    }


    private fun buildWiring(wiringFactory: WiringFactory, directiveWirings: List<SchemaDirectiveWiring>, runtimeWiringCustomizer: RuntimeWiringCustomizer): RuntimeWiring {
        val wiringBuilder = RuntimeWiring.newRuntimeWiring()
                .wiringFactory(wiringFactory)

        directiveWirings.map(wiringBuilder::directiveWiring)

        runtimeWiringCustomizer.customize(wiringBuilder)

        return wiringBuilder.build()
    }

    private fun transformFieldVisibility(graphQLSchema: GraphQLSchema, fieldVisibility: GraphqlFieldVisibility): GraphQLSchema {
        return graphQLSchema.transform { graphQLSchemaBuilder ->
            graphQLSchemaBuilder.codeRegistry(graphQLSchema.codeRegistry
                    .transform {
                        it.fieldVisibility(fieldVisibility)
                    })
        }
    }


    @Bean
    fun executionInputCustomizer(dataLoaderRegistryProvider: DataLoaderRegistryProvider): ExecutionInputCustomizer {
        return ExecutionInputCustomizer { executionInput: ExecutionInput, webRequest: WebRequest ->
            val transformedExecutionInput = executionInput.transform { executionInputBuilder ->
                //customize executionInput

                //give access to the HTTP request's headers
                //for federated tracing
                //https://github.com/apollographql/federation-jvm#federated-tracing
                val httpRequestHeaders = HTTPRequestHeaders {
                    webRequest.getHeader(it)
                }
                //TODO: use GraphQLContext instead
                executionInputBuilder.context(httpRequestHeaders)

                executionInputBuilder.dataLoaderRegistry(dataLoaderRegistryProvider.dataLoaderRegistry())

            }

            CompletableFuture.completedFuture(transformedExecutionInput)
        }
    }

    @ConditionalOnMissingBean(SchemaLoader::class)
    @Bean
    fun defaultSchemaLoader(): SchemaLoader {
        return DefaultSchemaLoader()
    }

    @Bean
    fun addFederatedTracing(): Instrumentation {
        return FederatedTracingInstrumentation(FederatedTracingInstrumentation.Options(true))
    }

    @Bean
    fun tracingInstrumentation() : Instrumentation {
        return TracingInstrumentation()
    }

    //Conditional on apm
    /*
    @ConditionalOnClass(name = [""])
    @Bean
    fun ignoreHealthCheckInNRInstrumentation(): Instrumentation {
        return IgnoreTransactionInstrumentation()
    }*/

    @Profile("Production", "prod")
    @ConditionalOnMissingBean(FieldVisibilityProvider::class)
    @Bean
    fun fieldVisibilityProvider(): FieldVisibilityProvider {
        return NoIntrospectionFieldVisibilityProvider()
    }

    @ConditionalOnMissingBean(FieldVisibilityProvider::class)
    @Bean
    fun defaultFieldVisibilityProvider(): FieldVisibilityProvider {
        return DefaultFieldVisibilityProvider()
    }

    @ConditionalOnMissingBean(AuthDirectiveConfig::class)
    @Bean
    fun defaultAuthDirectiveConfig(): AuthDirectiveConfig {
        return DefaultAuthDirectiveConfig()
    }

    @ConditionalOnBean(AuthDirectiveConfig::class)
    @Bean
    fun authDirectiveWiringFactory(authDirectiveConfig: AuthDirectiveConfig): AuthDirectiveWiringFactory {
        return AuthDirectiveWiringFactory(authDirectiveConfig)
    }

    @ConditionalOnMissingBean(WiringFactoryProvider::class)
    @Bean
    fun wiringFactoryProvider(wiringFactories: List<WiringFactory>, schemaResolverConfig: SchemaResolverConfig): WiringFactoryProvider {
        return DefaultWiringFactoryProvider(listOf(schemaResolverConfig.getDataFetcherWiringFactory(), schemaResolverConfig.getTypeResolverWiringFactory()).plus(wiringFactories))
    }

    @ConditionalOnMissingBean(SchemaDirectiveWiringProvider::class)
    @Bean
    fun defaultSchemaDirectiveWiringProvider(schemaDirectiveWirings: List<SchemaDirectiveWiring>): SchemaDirectiveWiringProvider {
        return DefaultSchemaDirectiveWiringProvider(schemaDirectiveWirings)
    }

    @Bean
    fun entityConfiguration(federatedEntityResolvers: Map<String, FederatedEntity<*>>): EntityConfigurationProvider {
        return EntityConfig(federatedEntityResolvers)
    }

    /**
     * [APQProperties.enabled]
     */
    @ConditionalOnProperty(prefix = "me.andrewberger.graphql.apq", name = ["enabled"], havingValue = "true")
    @Bean
    fun basicApolloAPQPersistedQueryCache(): BasicApolloAutomaticPersistedQueryCache {
        return BasicApolloAutomaticPersistedQueryCache()
    }

    @ConditionalOnBean(PersistedQueryCache::class)
    @ConditionalOnMissingBean(PreparsedDocumentProvider::class)
    @Bean
    fun apqPreparsedDocumentProvider(persistedQueryCache: PersistedQueryCache): PreparsedDocumentProvider {
        return ApolloPersistedQuerySupport(persistedQueryCache)
    }

    @ConditionalOnMissingBean(PreparsedDocumentProvider::class)
    @Bean
    fun preparsedDocumentProvider(): PreparsedDocumentProvider {
        return NoOpPreparsedDocumentProvider.INSTANCE
    }

    @ConditionalOnMissingBean(RuntimeWiringCustomizer::class)
    @Bean
    fun runtimeWiringCustomizer(): RuntimeWiringCustomizer {
        return object : RuntimeWiringCustomizer {
            override fun customize(runtimeWiring: RuntimeWiring.Builder): RuntimeWiring.Builder {
                return runtimeWiring
            }
        }
    }

    /*
    @Profile("Production", "prod")
    @ConditionalOnMissingBean(DataFetcherExceptionHandler::class)
    @Bean
    fun prodDataFetchingExceptionHandler(): DataFetcherExceptionHandler {
        
    }*/

    @ConditionalOnMissingBean(DataFetcherExceptionHandler::class)
    @Bean
    fun dataFetchingExceptionHandler(): DataFetcherExceptionHandler {
        return SimpleDataFetcherExceptionHandler()
    }


}