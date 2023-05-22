package me.andrewberger.graphql.util.dataloader

import org.dataloader.BatchLoader
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class DataLoaderRegistryFactoryTest {

    class TestKey
    class TestResult
    val expectedKey = "Key:${TestKey::class.java.canonicalName}-Type:${TestResult::class.java.canonicalName}"


    @Test
    fun dataLoaderRegistryTest() {

        val batchLoader = BatchLoader<TestKey, TestResult> { null }
        val dataLoaderWiring = DataLoaderWiring(TestKey::class, TestResult::class, batchLoader)
        val dataLoaderRegistry = DataLoaderRegistryFactory().dataLoaderRegistry(listOf(dataLoaderWiring))
        val dataLoader = dataLoaderRegistry.getDataLoader<TestKey, TestResult>(expectedKey)
        assertNotNull(dataLoader)
    }
}