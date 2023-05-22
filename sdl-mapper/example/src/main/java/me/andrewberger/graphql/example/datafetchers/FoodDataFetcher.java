package me.andrewberger.graphql.example.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.jetbrains.annotations.NotNull;

import me.andrewberger.graphql.annotations.DataFetchersFor;
import me.andrewberger.graphql.annotations.DataFetchersResolvedType;
import me.andrewberger.graphql.annotations.FieldDataFetcher;
import me.andrewberger.graphql.dataFetchers.DataFetchers;
import me.andrewberger.graphql.dataFetchers.FederatedEntity;
import me.andrewberger.graphql.example.dto.Food;
import me.andrewberger.graphql.example.service.FoodDescriptionService;
import me.andrewberger.graphql.util.dataloader.DataLoaderUtil;

@DataFetchersResolvedType(schemaTypeClass = Food.class)
@DataFetchersFor(schemaType = "Food")
public class FoodDataFetcher implements DataFetchers, FederatedEntity<Food> {

    private FoodDescriptionService foodDescriptionService;

    public FoodDataFetcher(FoodDescriptionService foodDescriptionService) {
        this.foodDescriptionService = foodDescriptionService;
    }

    @FieldDataFetcher(fieldName = "description")
    public DataFetcher<String> description() {
        return dataFetchingEnvironment -> {
            Food food = dataFetchingEnvironment.getSource();
            return foodDescriptionService.getDescriptionForFoodName(food.getName());
        };
    }

    @NotNull
    @Override
    public CompletableFuture<Food> resolveReference(@NotNull Map<String, ?> arguments, @NotNull DataFetchingEnvironment dataFetchingEnvironment) {
        DataLoader<String, Food> foodDataLoader = DataLoaderUtil.dataLoader(dataFetchingEnvironment, String.class, Food.class);
        return foodDataLoader.load((String) arguments.get("name"));
    }
}
