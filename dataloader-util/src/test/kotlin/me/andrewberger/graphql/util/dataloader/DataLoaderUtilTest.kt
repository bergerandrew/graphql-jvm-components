package me.andrewberger.graphql.util.dataloader

import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataLoaderUtilTest {

    class TestKey
    class TestResult
    val expectedKey = "Key:${TestKey::class.java.canonicalName}-Type:${TestResult::class.java.canonicalName}"

    @Test
    fun generateKeyTest() {
        val dataLoaderKey: String = dataLoaderKey(TestKey::class.java, TestResult::class.java)
        assertEquals(expectedKey, dataLoaderKey)
    }

    @Test
    fun retrieveDataLoaderTest() {
        val batchLoader = BatchLoader<TestKey, TestResult> { null }
        val dataLoader = DataLoader.newDataLoader(batchLoader)
        val dataLoaderRegistry = DataLoaderRegistry().register(expectedKey, dataLoader)
        val dataFetchingEnvironment = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .dataLoaderRegistry(dataLoaderRegistry)
                .build()

        val resultDataLoader = dataFetchingEnvironment.dataLoaderOf<TestKey, TestResult>()
        assertEquals(dataLoader, resultDataLoader)
    }

}