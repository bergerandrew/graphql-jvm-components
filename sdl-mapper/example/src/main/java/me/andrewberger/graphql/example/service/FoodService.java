package me.andrewberger.graphql.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import me.andrewberger.graphql.example.dto.Food;

@Service
public class FoodService {

    private Map<String, Food> foodMap = new HashMap<>();

    public FoodService() {
        Food pizza = new Food();
        pizza.setName("Pizza");
        pizza.setRating(10);
        foodMap.put(pizza.getName(), pizza);

        Food calzone = new Food();
        calzone.setName("Calzone");
        calzone.setRating(8);
        foodMap.put(calzone.getName(), calzone);
    }

    public List<Food> getFoodsByName(List<String> foodNames) {
        return foodNames.stream().map((foodName) -> foodMap.get(foodName)).collect(Collectors.toList());
    }

    public Food saveFood(Food food) {
        foodMap.put(food.getName(), food);
        return food;
    }
}
