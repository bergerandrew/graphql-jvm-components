package me.andrewberger.graphql.directives.auth

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger(AuthDirectiveWiring::class.java)

class AuthDirectiveWiring(val authDirectiveConfig: AuthDirectiveConfig) : SchemaDirectiveWiring {
    //https://www.graphql-java.com/documentation/v15/sdl-directives/
    //https://www.apollographql.com/docs/apollo-server/schema/creating-directives/#enforcing-access-permissions

    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        return if (authDirectiveConfig.hasAuthDirective(environment)) {
            val fieldDataFetcher = environment.fieldDataFetcher

            logger.info("Setting field level auth for: ${environment.fieldsContainer.name}.${environment.fieldDefinition.name}")
            environment.setFieldDataFetcher(getDataFetcherAuthWrapper(fieldDataFetcher, environment))

        } else {
            environment.fieldDefinition
        }
    }

    override fun onObject(environment: SchemaDirectiveWiringEnvironment<GraphQLObjectType>): GraphQLObjectType {
        if (authDirectiveConfig.hasAuthDirective(environment)) {

            val codeRegistry = environment.codeRegistry

            val fieldsContainer = environment.fieldsContainer

            fieldsContainer.fieldDefinitions
                    //if we already set auth on the field we dont want to overwrite it with different roles
                    .filterNot { codeRegistry.getDataFetcher(fieldsContainer, it) is AuthDataFetcher }
                    .forEach { fieldDefinition ->
                        logger.info("Setting object level auth for: ${environment.fieldsContainer.name}.${fieldDefinition.name}")
                        codeRegistry.dataFetcher(fieldsContainer, fieldDefinition, getDataFetcherAuthWrapper(codeRegistry.getDataFetcher(fieldsContainer, fieldDefinition), environment))
                    }
        }
        return environment.element
    }


    private fun getDataFetcherAuthWrapper(dataFetcher: DataFetcher<*>, environment: SchemaDirectiveWiringEnvironment<*>): AuthDataFetcher<Any> {
        return object : AuthDataFetcher<Any> {
            override fun get(dataFetchingEnvironment: DataFetchingEnvironment): Any {
                return authDirectiveConfig.dataFetcherAuthWrapper(dataFetcher, dataFetchingEnvironment, environment)
            }
        }
    }
}

interface AuthDataFetcher<T> : DataFetcher<T>

