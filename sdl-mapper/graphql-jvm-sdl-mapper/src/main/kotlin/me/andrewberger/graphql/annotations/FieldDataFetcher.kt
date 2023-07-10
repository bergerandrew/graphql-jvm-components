package me.andrewberger.graphql.annotations

/**
 * This annotation is designed to be used on classes which implement
 * [me.andrewberger.graphql.dataFetchers.DataFetchers] and include a [DataFetchersFor] annotation.
 * Each function that resolves a field should include this annotation and specify the field name from the
 * schema as the fieldName if it does not match the function name.
 */
annotation class FieldDataFetcher(val fieldName: String = "")
