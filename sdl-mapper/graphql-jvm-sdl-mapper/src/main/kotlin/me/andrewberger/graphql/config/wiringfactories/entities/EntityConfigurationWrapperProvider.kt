package me.andrewberger.graphql.config.wiringfactories.entities

import me.andrewberger.graphql.federation.FederationTransformerBuilder

interface EntityConfigurationProvider {
    fun entityConfiguration(): FederationTransformerBuilder
}