package me.andrewberger.graphql.apq

import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.function.BiFunction

class BasicApolloAutomaticPersistedQueryCacheRepositoryTest {

    @Test
    fun cacheMissTest() {
        val basicApolloAPQCacheRepository = BasicApolloAutomaticPersistedQueryCacheRepository<String, Any>()
        val key = "foo"
        val computeFunction = mockkClass(BiFunction::class) as BiFunction<in String?, in Any?, out Any?>
        val persistedQueryNotFoundException = PersistedQueryNotFound(key)
        every { computeFunction.apply(key, any()) } throws persistedQueryNotFoundException

        val computeResult = basicApolloAPQCacheRepository.runCatching {
            compute(key, computeFunction)
        }

        assertEquals(persistedQueryNotFoundException, computeResult.exceptionOrNull())
        verify(exactly = 1) {
            computeFunction.apply(key, any())
        }
    }


    @Test
    fun cacheMissAndSetTest() {
        val basicApolloAPQCacheRepository = BasicApolloAutomaticPersistedQueryCacheRepository<String, Any>()
        val key = "foo"
        val computeFunction = mockkClass(BiFunction::class) as BiFunction<in String?, in Any?, out Any?>
        every { computeFunction.apply(key, any()) } returns "Hello world"

        basicApolloAPQCacheRepository.compute(key, computeFunction)
        verify(exactly = 1) {
            computeFunction.apply(key, any())
        }
    }


    @Test
    fun cacheHitTest() {
        val basicApolloAPQCacheRepository = BasicApolloAutomaticPersistedQueryCacheRepository<String, Any>()
        val key = "foo"
        val value = "Hello world"

        basicApolloAPQCacheRepository.compute(key) { _, _ ->
            value
        }

        val computeFunction = mockkClass(BiFunction::class) as BiFunction<in String?, in Any?, out Any?>

        every { computeFunction.apply(key, value) } returns "Hello world"

        basicApolloAPQCacheRepository.compute(key, computeFunction)
        verify(exactly = 1) {
            computeFunction.apply(key, value)
        }
    }
}