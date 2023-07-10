package me.andrewberger.graphql.federation.example

import me.andrewberger.graphql.federation.EntityDataFetcher
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class IngredientEntityResolver: EntityDataFetcher<CompletableFuture<Ingredient>?> {
    override fun resolveReference(args: Map<String, Any>, dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<Ingredient>? {
        val ingredientName = args["name"] as String
        val ingredientDataLoader = dataFetchingEnvironment.getDataLoader<String, Ingredient>("example.ingredient")
        return ingredientDataLoader.load(ingredientName)
    }
}

class IngredientBatchLoader: BatchLoader<String, Ingredient> {
    override fun load(names: List<String>): CompletionStage<List<Ingredient>> {
        return CompletableFuture.supplyAsync { names.map { ingredientName -> Ingredient(name = ingredientName) } }
    }
}