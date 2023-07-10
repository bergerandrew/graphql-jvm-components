package me.andrewberger.graphql.federation

import com.apollographql.federation.graphqljava.SchemaTransformer

class NoOpSchemaTransformer: SchemaTransformerCustomizer {
    override fun customizeSchemaTransformer(schemaTransformer: SchemaTransformer): SchemaTransformer {
        return schemaTransformer
    }
}