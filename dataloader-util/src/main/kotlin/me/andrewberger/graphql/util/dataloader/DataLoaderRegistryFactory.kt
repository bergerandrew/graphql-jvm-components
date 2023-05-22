package me.andrewberger.graphql.util.dataloader

import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry

class DataLoaderRegistryFactory() {
    fun dataLoaderRegistry(vararg dataLoaderWirings: DataLoaderWiring): DataLoaderRegistry {
        return dataLoaderRegistry(dataLoaderWirings.asList())
    }

    fun dataLoaderRegistry(dataLoaderWirings: Collection<DataLoaderWiring>): DataLoaderRegistry {
        val dataLoaderRegistry = DataLoaderRegistry()
        dataLoaderWirings.forEach {
            val dataLoaderKey = dataLoaderKey(it.key.java, it.type.java)
            dataLoaderRegistry.register(dataLoaderKey, DataLoader(it.batchLoader))
        }
        return dataLoaderRegistry
    }
}