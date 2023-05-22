package me.andrewberger.graphql.federation.example

import graphql.schema.DataFetcher
import java.util.concurrent.CompletableFuture

class IngredientDataFetcher {


    fun getIngredient(): DataFetcher<CompletableFuture<Ingredient>> {
        return DataFetcher<CompletableFuture<Ingredient>> {
            val ingredientDataLoader = it.getDataLoader<String, Ingredient>("example.ingredient")
            ingredientDataLoader.load(it.getArgument("name"))
        }
    }

}
