package me.andrewberger.graphql.directives.auth

import graphql.language.FieldDefinition
import graphql.schema.*
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.SchemaDirectiveWiringEnvironmentImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito

class AuthDirectiveWiringTest {

    @Test
    fun onFieldWithDirectiveTest() {
        val authDirectiveConfig = DefaultAuthDirectiveConfig()
        val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)
        val mockSchemaDirectiveWiringEnvironment = Mockito.mock(SchemaDirectiveWiringEnvironment::class.java) as SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>
        val dataFetcher = DataFetcher { null }
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.containsDirective("auth")).thenReturn(true)
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldDataFetcher).thenReturn(dataFetcher)
        val mockFieldsContainer = Mockito.mock(GraphQLFieldsContainer::class.java)
        Mockito.`when`(mockFieldsContainer.name).thenReturn("Test")
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldsContainer).thenReturn(mockFieldsContainer)
        val mockFieldDefinition = Mockito.mock(GraphQLFieldDefinition::class.java)
        Mockito.`when`(mockFieldDefinition.name).thenReturn("testField")
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldDefinition).thenReturn(mockFieldDefinition)

        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.setFieldDataFetcher(Mockito.any(AuthDataFetcher::class.java))).thenReturn(mockFieldDefinition)

        val fieldDefinition = authDirectiveWiring.onField(mockSchemaDirectiveWiringEnvironment)
        Mockito.verify(mockSchemaDirectiveWiringEnvironment).setFieldDataFetcher(Mockito.any(AuthDataFetcher::class.java))
    }

    @Test
    fun onFieldWithOutDirectiveTest() {
        val authDirectiveConfig = DefaultAuthDirectiveConfig()
        val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)
        val mockSchemaDirectiveWiringEnvironment = Mockito.mock(SchemaDirectiveWiringEnvironment::class.java) as SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>
        val dataFetcher = DataFetcher { null }
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.containsDirective("auth")).thenReturn(false)
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldDataFetcher).thenReturn(dataFetcher)
        val mockFieldsContainer = Mockito.mock(GraphQLFieldsContainer::class.java)
        Mockito.`when`(mockFieldsContainer.name).thenReturn("Test")
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldsContainer).thenReturn(mockFieldsContainer)
        val mockFieldDefinition = Mockito.mock(GraphQLFieldDefinition::class.java)
        Mockito.`when`(mockFieldDefinition.name).thenReturn("testField")
        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.fieldDefinition).thenReturn(mockFieldDefinition)

        Mockito.`when`(mockSchemaDirectiveWiringEnvironment.setFieldDataFetcher(Mockito.any(AuthDataFetcher::class.java))).thenReturn(mockFieldDefinition)

        val fieldDefinition = authDirectiveWiring.onField(mockSchemaDirectiveWiringEnvironment)
        Mockito.verify(mockSchemaDirectiveWiringEnvironment, Mockito.times(0)).setFieldDataFetcher(Mockito.any(AuthDataFetcher::class.java))
        assertEquals(mockFieldDefinition, fieldDefinition)
    }


    @Test
    fun onObjectWithDirectiveTest() {
        val authDirectiveConfig = DefaultAuthDirectiveConfig()
        val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)
        val dataFetcher = DataFetcher { null }

        val mockCodeRegistry = Mockito.mock(GraphQLCodeRegistry.Builder::class.java)
                .apply {
                    Mockito.`when`(getDataFetcher(Mockito.any(GraphQLFieldsContainer::class.java), Mockito.any(GraphQLFieldDefinition::class.java))).thenReturn(dataFetcher)
                }

        val mockTestFieldDefinition = Mockito.mock(GraphQLFieldDefinition::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("testField")
                }

        val fieldDefinitions = listOf(
                mockTestFieldDefinition
        )

        val mockFieldsContainer = Mockito.mock(GraphQLFieldsContainer::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("Test")
                    Mockito.`when`(getFieldDefinitions()).thenReturn(fieldDefinitions)
                }


        val mockSchemaDirectiveWiringEnvironment = Mockito.mock(SchemaDirectiveWiringEnvironment::class.java)
                .apply {
                    Mockito.`when`(containsDirective("auth")).thenReturn(true)
                    Mockito.`when`(codeRegistry).thenReturn(mockCodeRegistry)
                    Mockito.`when`(fieldsContainer).thenReturn(mockFieldsContainer)
                    Mockito.`when`(element).thenReturn(Mockito.mock(GraphQLObjectType::class.java))
                } as SchemaDirectiveWiringEnvironment<GraphQLObjectType>


        val objectType = authDirectiveWiring.onObject(mockSchemaDirectiveWiringEnvironment)
        Mockito.verify(mockCodeRegistry).dataFetcher(Mockito.eq(mockFieldsContainer), Mockito.eq(mockTestFieldDefinition), Mockito.any(AuthDataFetcher::class.java))
    }

    @Test
    fun onObjectWithDirectiveNoOverwriteTest() {
        val authDirectiveConfig = DefaultAuthDirectiveConfig()
        val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)
        val dataFetcher = Mockito.mock(AuthDataFetcher::class.java)

        val mockTestFieldDefinition = Mockito.mock(GraphQLFieldDefinition::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("testField")
                }

        val fieldDefinitions = listOf(
                mockTestFieldDefinition
        )

        val mockFieldsContainer = Mockito.mock(GraphQLFieldsContainer::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("Test")
                    Mockito.`when`(getFieldDefinitions()).thenReturn(fieldDefinitions)
                }

        val mockCodeRegistry = Mockito.mock(GraphQLCodeRegistry.Builder::class.java)
                .apply {
                    Mockito.`when`(getDataFetcher(Mockito.eq(mockFieldsContainer), Mockito.eq(mockTestFieldDefinition))).thenReturn(dataFetcher)
                }

        val mockSchemaDirectiveWiringEnvironment = Mockito.mock(SchemaDirectiveWiringEnvironment::class.java)
                .apply {
                    Mockito.`when`(containsDirective("auth")).thenReturn(true)
                    Mockito.`when`(codeRegistry).thenReturn(mockCodeRegistry)
                    Mockito.`when`(fieldsContainer).thenReturn(mockFieldsContainer)
                    Mockito.`when`(element).thenReturn(Mockito.mock(GraphQLObjectType::class.java))
                } as SchemaDirectiveWiringEnvironment<GraphQLObjectType>


        val objectType = authDirectiveWiring.onObject(mockSchemaDirectiveWiringEnvironment)
        Mockito.verify(mockCodeRegistry, Mockito.times(0)).dataFetcher(Mockito.any(), Mockito.any(), Mockito.any())
    }

    @Test
    fun onObjectWithoutDirectiveTest() {
        val authDirectiveConfig = DefaultAuthDirectiveConfig()
        val authDirectiveWiring = AuthDirectiveWiring(authDirectiveConfig)
        val dataFetcher = DataFetcher { null }

        val mockCodeRegistry = Mockito.mock(GraphQLCodeRegistry.Builder::class.java)
                .apply {
                    Mockito.`when`(getDataFetcher(Mockito.any(GraphQLFieldsContainer::class.java), Mockito.any(GraphQLFieldDefinition::class.java))).thenReturn(dataFetcher)
                }

        val mockTestFieldDefinition = Mockito.mock(GraphQLFieldDefinition::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("testField")
                }

        val fieldDefinitions = listOf(
                mockTestFieldDefinition
        )

        val mockFieldsContainer = Mockito.mock(GraphQLFieldsContainer::class.java)
                .apply {
                    Mockito.`when`(name).thenReturn("Test")
                    Mockito.`when`(getFieldDefinitions()).thenReturn(fieldDefinitions)
                }


        val mockSchemaDirectiveWiringEnvironment = Mockito.mock(SchemaDirectiveWiringEnvironment::class.java)
                .apply {
                    Mockito.`when`(containsDirective("auth")).thenReturn(false)
                    Mockito.`when`(codeRegistry).thenReturn(mockCodeRegistry)
                    Mockito.`when`(fieldsContainer).thenReturn(mockFieldsContainer)
                    Mockito.`when`(element).thenReturn(Mockito.mock(GraphQLObjectType::class.java))
                } as SchemaDirectiveWiringEnvironment<GraphQLObjectType>


        val objectType = authDirectiveWiring.onObject(mockSchemaDirectiveWiringEnvironment)
        Mockito.verify(mockCodeRegistry, Mockito.times(0)).dataFetcher(Mockito.any(), Mockito.any(), Mockito.any())
    }

}