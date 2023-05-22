package me.andrewberger.graphql.printschema

import com.apollographql.federation.graphqljava.Federation
import com.apollographql.federation.graphqljava.SchemaTransformer
import graphql.Assert
import graphql.schema.TypeResolver
import graphql.schema.idl.WiringFactory
import graphql.schema.idl.InterfaceWiringEnvironment
import graphql.schema.idl.UnionWiringEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.visibility.BlockedFields
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File


@Mojo(name = "print-schema")
class PrintSchema : AbstractMojo() {

    @Parameter(defaultValue = "\${project.build.outputDirectory}", readonly = true)
    lateinit var buildDirectory: File

    /**
     * Directory to search for .graphqls files
     */
    @Parameter(property = "schemaFolderPath", defaultValue = "schema/")
    lateinit var schemaFolderPath: String

    /**
     * Directory to write schema.graphqls file
     */
    @Parameter(property = "outputFolderPath")
    lateinit var  outputFolderPath: String


    @Throws(MojoExecutionException::class)
    override fun execute() {

        log.info("Starting Print Schema")

        val absoluteSchemaFolderPath = File(buildDirectory, schemaFolderPath)
        val absoluteOutputFolderPath = File(buildDirectory, outputFolderPath)

        log.info("Checking path for SDL files: ${absoluteSchemaFolderPath.absolutePath}")

        kotlin.runCatching {
            val schemaString = getSchemaString(absoluteSchemaFolderPath)
            log.debug("Finished retrieving schema files")

            writeSchemaFile(schemaString, absoluteOutputFolderPath)
            log.debug("Finished writing schema file")
        }.onFailure { exception ->
            log.error("Failed to run print schema: ${exception.message}")
            exception.printStackTrace()
            throw MojoExecutionException("Failed to run print schema", exception)
        }
        log.info("Print Schema Done.")
    }

    private fun getSchemaString(absoluteSchemaFolderPath: File): String {
        val sdlFiles = absoluteSchemaFolderPath.walkTopDown().filter { it.extension == "graphqls" }

        log.info("SDL Files:")
        for (sdlFile in sdlFiles) {
            log.info("Found SDL file: ${sdlFile.absolutePath}")
        }
        val schemaParser = SchemaParser()
        val fileTypeDefinitionRegistries = sdlFiles.map(schemaParser::parse)
        val typeRegistry: TypeDefinitionRegistry = TypeDefinitionRegistry().apply { fileTypeDefinitionRegistries.forEach { merge(it) } }

        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .wiringFactory(object : WiringFactory {
                    val dummyTypeResolver = TypeResolver { Assert.assertShouldNeverHappen() }
                    override fun providesTypeResolver(environment: InterfaceWiringEnvironment): Boolean { return true }
                    override fun providesTypeResolver(environment: UnionWiringEnvironment): Boolean { return true }
                    override fun getTypeResolver(environment: InterfaceWiringEnvironment): TypeResolver { return dummyTypeResolver }
                    override fun getTypeResolver(environment: UnionWiringEnvironment): TypeResolver { return dummyTypeResolver }
                })
                .build()

/*        val schemaGenerator = SchemaGenerator()
        val schema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)*/
        val schemaTransformer = Federation.transform(typeRegistry, runtimeWiring)
        schemaTransformer.fetchEntitiesFactory{ Assert.assertShouldNeverHappen() }
        schemaTransformer.resolveEntityType { Assert.assertShouldNeverHappen() }

        val schema = schemaTransformer.build()

        //The schema printer is not removing _entities and _service
        //but the PR mentions using it for schema check: https://github.com/apollographql/federation-jvm/pull/53
        val queryTypeName = schema.queryType.name
        val blockedFields = BlockedFields.newBlock().run {

            addPattern("$queryTypeName._entities")
            addPattern("$queryTypeName._service")
            build()
        }
        val transformedSchema = schema.transform { schemaBuilder ->
            val graphQLCodeRegistry = schema.codeRegistry.transform {
                it.fieldVisibility(blockedFields)
            }
            schemaBuilder.codeRegistry(graphQLCodeRegistry).build()
        }

//        return SchemaPrinter().print(schema)
        return SchemaTransformer.sdl(transformedSchema)
    }

    private fun writeSchemaFile(schemaString: String, absoluteOutputFolderPath: File) {

        val schemaFile = File(absoluteOutputFolderPath, "schema.graphqls")
        log.info("Writing schema.graphqls to ${schemaFile.absolutePath}")
        if (schemaFile.exists()) {
            log.info("File schema.graphqls already exists.  Deleting...")
            schemaFile.delete()
            log.info("File schema.graphqls deleted.")
        }
        schemaFile.createNewFile()
        schemaFile.writeText(schemaString)
        log.info("File schema.graphqls written.")
    }

}