package me.andrewberger.graphql.util.dataloader

import org.dataloader.BatchLoader
import kotlin.reflect.KClass

class DataLoaderWiring(val key: KClass<*>, val type: KClass<*>, val batchLoader: BatchLoader<*, *>)

fun <Key : Any, Type : Any> newDataLoaderWiring(key: KClass<Key>, type: KClass<Type>, batchLoader: BatchLoader<Key, Type>): DataLoaderWiring {
    return DataLoaderWiring(key, type, batchLoader)
}

inline fun <reified Key: Any, reified Type: Any> dataLoaderWiringOf(batchLoader: BatchLoader<Key, Type>): DataLoaderWiring {
    return DataLoaderWiring(Key::class,  Type::class, batchLoader)
}