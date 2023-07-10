package me.andrewberger.graphql.config.schemaloader

import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import java.io.Reader

class DefaultSchemaLoader : SchemaLoader {
    override fun load(): List<Reader> {
        val cl = this.javaClass.classLoader
        val resolver: ResourcePatternResolver = PathMatchingResourcePatternResolver(cl)
        return resolver.getResources("${ResourceLoader.CLASSPATH_URL_PREFIX}/schema/*.graphqls").asList()
                .filter { it.isReadable }
                .map { it.inputStream.reader() }
    }
}