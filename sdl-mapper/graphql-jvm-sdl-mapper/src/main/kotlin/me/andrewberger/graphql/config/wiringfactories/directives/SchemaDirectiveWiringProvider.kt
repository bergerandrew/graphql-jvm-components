package me.andrewberger.graphql.config.wiringfactories.directives

import graphql.schema.idl.SchemaDirectiveWiring

interface SchemaDirectiveWiringProvider {
    fun schemaDirectiveWirings(): List<SchemaDirectiveWiring>
}