package me.andrewberger.graphql.example.batchloaders;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.dataloader.BatchLoader;

import me.andrewberger.graphql.annotations.DataLoader;
import me.andrewberger.graphql.example.dto.Food;
import me.andrewberger.graphql.example.service.FoodService;

@DataLoader(key = String.class, type = Food.class)
public class FoodBatchLoader implements BatchLoader<String, Food> {

    private final FoodService foodService;

    public FoodBatchLoader(FoodService foodService) {
        this.foodService = foodService;
    }

    @Override
    public CompletionStage<List<Food>> load(List<String> foodNames) {
        return CompletableFuture.supplyAsync(() -> foodService.getFoodsByName(foodNames));
    }
}
