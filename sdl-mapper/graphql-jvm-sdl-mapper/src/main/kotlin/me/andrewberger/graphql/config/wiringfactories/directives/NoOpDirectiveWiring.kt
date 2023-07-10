package me.andrewberger.graphql.config.wiringfactories.directives

import graphql.schema.idl.SchemaDirectiveWiring
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@ConditionalOnMissingBean(SchemaDirectiveWiring::class)
@Component
class NoOpDirectiveWiring : SchemaDirectiveWiring
