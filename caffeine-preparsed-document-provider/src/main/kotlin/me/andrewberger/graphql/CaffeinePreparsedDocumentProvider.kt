package me.andrewberger.graphql

import com.github.benmanes.caffeine.cache.Cache
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import org.slf4j.LoggerFactory
import java.util.function.Function


private val logger = LoggerFactory.getLogger(CaffeinePreparsedDocumentProvider::class.java)


class CaffeinePreparsedDocumentProvider(private val cache: Cache<String, PreparsedDocumentEntry>): PreparsedDocumentProvider {
    override fun getDocument(executionInput: ExecutionInput, computeFunction: Function<ExecutionInput, PreparsedDocumentEntry>): PreparsedDocumentEntry? {
        logger.debug("Entering CaffeinePreparsedDocumentProvider")
        val mapCompute: (key: String)->PreparsedDocumentEntry = { key ->
            logger.debug("CaffeinePreparsedDocumentProvider cache missed.  PreparsedDocumentEntry will be computed.")
            computeFunction.apply(executionInput)
        }
        return cache[executionInput.query, mapCompute]
    }
}