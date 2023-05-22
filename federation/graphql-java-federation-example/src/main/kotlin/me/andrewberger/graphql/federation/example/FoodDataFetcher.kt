package me.andrewberger.graphql.federation.example

import graphql.schema.DataFetcher
import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class FoodDataFetcher {
    fun getFood(): DataFetcher<CompletableFuture<Food>> {
        return DataFetcher<CompletableFuture<Food>> {
            val foodDataLoader = it.getDataLoader<String, Food>("example.food")
            foodDataLoader.load(it.getArgument("name"))
        }
    }
}

class FoodBatchLoader : BatchLoader<String, Any> {
    override fun load(names: List<String>): CompletionStage<List<Any>> {
        return CompletableFuture.supplyAsync {
            names.map { foodName ->
                Food(name = foodName, ingredient = Ingredient("Tomato"))
            }
        }
    }
}
