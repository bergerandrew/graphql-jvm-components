package me.andrewberger.graphql.config.wiringfactories

import graphql.schema.idl.WiringFactory


interface WiringFactoryProvider {
    fun wiringFactories(): WiringFactory
}