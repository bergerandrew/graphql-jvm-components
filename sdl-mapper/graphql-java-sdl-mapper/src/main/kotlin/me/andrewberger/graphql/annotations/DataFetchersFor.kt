package me.andrewberger.graphql.annotations

import org.springframework.stereotype.Component

/**
 * Marks the class as a Spring [Component] that defines [FieldDataFetcher]s.
 */
@Component
annotation class DataFetchersFor(val schemaType: String)