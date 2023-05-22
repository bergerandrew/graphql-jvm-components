package me.andrewberger.graphql.config.dataloaders

import me.andrewberger.graphql.util.dataloader.DataLoaderRegistryFactory
import me.andrewberger.graphql.util.dataloader.DataLoaderWiring
import org.dataloader.BatchLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoaderRegistryConfig(val batchLoaders: Map<String, BatchLoader<*, *>>) {

    val dataLoaderWirings = batchLoaders.filter { it.value.javaClass.isAnnotationPresent(me.andrewberger.graphql.annotations.DataLoader::class.java) }
            .map { (beanName, batchLoader) ->
                val dataLoaderComponent = batchLoader.javaClass.getAnnotation(me.andrewberger.graphql.annotations.DataLoader::class.java)
                val registryKey = dataLoaderComponent.key
                val registryType = dataLoaderComponent.type

                DataLoaderWiring(registryKey, registryType, batchLoader)
            }


    @Bean
    fun dataLoaderRegistryProvider(): DataLoaderRegistryProvider {

        return object : DataLoaderRegistryProvider {
            override fun dataLoaderRegistry(): DataLoaderRegistry {
                return DataLoaderRegistryFactory().dataLoaderRegistry(dataLoaderWirings)
            }
        }
    }

}
