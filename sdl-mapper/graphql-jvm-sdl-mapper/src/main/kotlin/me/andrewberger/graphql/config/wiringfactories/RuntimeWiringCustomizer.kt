package me.andrewberger.graphql.config.wiringfactories

import graphql.schema.idl.RuntimeWiring

interface RuntimeWiringCustomizer {
    fun customize(runtimeWiring: RuntimeWiring.Builder): RuntimeWiring.Builder
}