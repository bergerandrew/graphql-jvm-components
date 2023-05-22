package me.andrewberger.graphql.config.dataloaders

import org.dataloader.DataLoaderRegistry

interface DataLoaderRegistryProvider {
    fun dataLoaderRegistry(): DataLoaderRegistry
}