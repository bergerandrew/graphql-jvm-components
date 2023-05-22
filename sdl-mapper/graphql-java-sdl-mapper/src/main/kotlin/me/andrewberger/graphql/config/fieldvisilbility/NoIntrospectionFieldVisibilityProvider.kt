package me.andrewberger.graphql.config.fieldvisilbility

import graphql.schema.visibility.GraphqlFieldVisibility
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility

class NoIntrospectionFieldVisibilityProvider : FieldVisibilityProvider {
    override fun get(): GraphqlFieldVisibility {
        return NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY
    }
}