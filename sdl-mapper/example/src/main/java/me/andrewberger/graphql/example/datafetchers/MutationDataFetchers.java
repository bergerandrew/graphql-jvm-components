package me.andrewberger.graphql.example.datafetchers;

import graphql.schema.DataFetcher;

import java.util.Map;

import me.andrewberger.graphql.annotations.DataFetchersFor;
import me.andrewberger.graphql.annotations.FieldDataFetcher;
import me.andrewberger.graphql.dataFetchers.DataFetchers;
import me.andrewberger.graphql.example.dto.Food;
import me.andrewberger.graphql.example.service.FoodService;

@DataFetchersFor(schemaType = "MutationType")
public class MutationDataFetchers implements DataFetchers {

    private final FoodService foodService;

    public MutationDataFetchers(FoodService foodService) {
        this.foodService = foodService;
    }

    @FieldDataFetcher(fieldName = "addFood")
    public DataFetcher<Food> addFood() {
        return dataFetchingEnvironment -> {
            Map<String, Object> foodInput = dataFetchingEnvironment.getArgument("food");
            Food food = new Food();
            food.setName((String) foodInput.get("name"));
            food.setRating((Integer) foodInput.get("rating"));
            return foodService.saveFood(food);
        };
    }

}
