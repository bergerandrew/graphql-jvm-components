package me.andrewberger.graphql.config.schemaloader

import java.io.InputStreamReader
import java.io.Reader

interface SchemaLoader {

    fun load(): List<Reader>
}