package me.andrewberger.graphql.example.service;

import org.springframework.stereotype.Service;

@Service
public class FoodDescriptionService {

    public String getDescriptionForFoodName(String foodName) {
        String description = "This food is not a Pizza";
        if ("Pizza".equalsIgnoreCase(foodName)) {
            description = "This food is a Pizza";
        }
        return description;
    }

}
