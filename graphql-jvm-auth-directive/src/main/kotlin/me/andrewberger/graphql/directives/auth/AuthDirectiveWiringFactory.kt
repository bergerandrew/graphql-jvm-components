package me.andrewberger.graphql.directives.auth

import me.andrewberger.graphql.directives.auth.AuthDirectiveConfig
import me.andrewberger.graphql.directives.auth.AuthDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.WiringFactory


class AuthDirectiveWiringFactory(private val authDirectiveConfig: AuthDirectiveConfig) : WiringFactory {
    val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)

    override fun getSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): SchemaDirectiveWiring? {
        return if (authDirectiveConfig.hasAuthDirective(environment)) {
            authDirectiveWiring
        } else null
    }

    override fun providesSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): Boolean {
        return authDirectiveConfig.hasAuthDirective(environment)
    }
}
