package me.andrewberger.graphql.config.wiringfactories.directives

import graphql.schema.idl.SchemaDirectiveWiring

class DefaultSchemaDirectiveWiringProvider(val schemaDirectiveWirings: List<SchemaDirectiveWiring>) : SchemaDirectiveWiringProvider {
    override fun schemaDirectiveWirings(): List<SchemaDirectiveWiring> {
        return schemaDirectiveWirings
    }
}