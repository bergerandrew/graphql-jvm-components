package me.andrewberger.graphql.util.dataloader

import org.dataloader.BatchLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataLoaderWiringTest {

    class TestKey
    class TestResult

    @Test
    fun reifiedDataLoaderWiringTest() {
        val batchLoader = BatchLoader<TestKey, TestResult> { null }
        val dataLoaderWiring = dataLoaderWiringOf(batchLoader)
        assertEquals(TestKey::class, dataLoaderWiring.key)
        assertEquals(TestResult::class, dataLoaderWiring.type)
        assertEquals(batchLoader, dataLoaderWiring.batchLoader)
    }

    @Test
    fun dataLoaderWiringConstructorTest() {
        val batchLoader = BatchLoader<TestKey, TestResult> { null }
        val dataLoaderWiring = DataLoaderWiring(TestKey::class, TestResult::class, batchLoader)
        assertEquals(TestKey::class, dataLoaderWiring.key)
        assertEquals(TestResult::class, dataLoaderWiring.type)
        assertEquals(batchLoader, dataLoaderWiring.batchLoader)
    }

    @Test
    fun newDataLoaderWiringTest() {
        val batchLoader = BatchLoader<TestKey, TestResult> { null }
        val dataLoaderWiring = newDataLoaderWiring(TestKey::class, TestResult::class, batchLoader)
        assertEquals(TestKey::class, dataLoaderWiring.key)
        assertEquals(TestResult::class, dataLoaderWiring.type)
        assertEquals(batchLoader, dataLoaderWiring.batchLoader)
    }

}