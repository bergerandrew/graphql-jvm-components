@file:JvmName("DataLoaderUtil")

package me.andrewberger.graphql.util.dataloader

import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import kotlin.reflect.jvm.jvmName

inline fun <reified K, reified T> DataFetchingEnvironment.dataLoaderOf(): DataLoader<K, T>? {
    return dataLoader(K::class.java, T::class.java)
}

fun <K, T> DataFetchingEnvironment.dataLoader(key: Class<K>, type: Class<T>): DataLoader<K, T>? {
    return getDataLoader<K, T>("Key:${key.canonicalName}-Type:${type.canonicalName}")
}


inline fun <reified K, reified T> dataLoaderKeyOf(): String {
    return dataLoaderKey(K::class.java, T::class.java)
}

fun <K, T> dataLoaderKey(key: Class<K>, type: Class<T>): String {
    return "Key:${key.canonicalName}-Type:${type.canonicalName}"
}