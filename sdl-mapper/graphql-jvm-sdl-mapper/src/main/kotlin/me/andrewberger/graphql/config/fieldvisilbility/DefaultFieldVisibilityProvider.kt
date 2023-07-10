package me.andrewberger.graphql.config.fieldvisilbility

import graphql.schema.visibility.DefaultGraphqlFieldVisibility
import graphql.schema.visibility.GraphqlFieldVisibility

class DefaultFieldVisibilityProvider() : FieldVisibilityProvider {
    override fun get(): GraphqlFieldVisibility {
        return DefaultGraphqlFieldVisibility()
    }
}