package me.andrewberger.graphql.config.wiringfactories

import graphql.schema.idl.CombinedWiringFactory
import graphql.schema.idl.WiringFactory

class DefaultWiringFactoryProvider(val wiringFactories: List<WiringFactory>) : WiringFactoryProvider {
    override fun wiringFactories(): WiringFactory {
        return CombinedWiringFactory(wiringFactories)
    }

}