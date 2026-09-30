package com.zoo.zoo.model.feeding;

import lombok.AllArgsConstructor;
import lombok.Getter;

// HINT: Each food type now carries its category; FeedingService no longer keeps its own insect/meat lists.
// HINT: Any food type that exists after your merge needs a category - and the category decides which animals may eat it.
@Getter
@AllArgsConstructor

public enum FoodType {
    COCKROACH(FoodCategory.INSECT),
    CRICKET(FoodCategory.INSECT),
    MEALWORM(FoodCategory.INSECT),
    MOUSE(FoodCategory.MEAT),
    RAT(FoodCategory.MEAT),
    CHICKEN(FoodCategory.MEAT),
    RABBIT(FoodCategory.MEAT);

    private final FoodCategory category;
}
