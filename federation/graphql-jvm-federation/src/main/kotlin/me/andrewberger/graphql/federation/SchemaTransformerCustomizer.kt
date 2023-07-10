package me.andrewberger.graphql.federation

import com.apollographql.federation.graphqljava.SchemaTransformer

interface SchemaTransformerCustomizer {
    fun customizeSchemaTransformer(schemaTransformer: SchemaTransformer): SchemaTransformer

}