package me.andrewberger.graphql.config.fieldvisilbility

import graphql.schema.visibility.GraphqlFieldVisibility

interface FieldVisibilityProvider {
    fun get(): GraphqlFieldVisibility
}
