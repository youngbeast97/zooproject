package com.zoo.zoo.model.feeding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum FoodType {
    // HINT: LOCUST is an insect - supplier switched part of the cricket orders to locusts.
    COCKROACH, CRICKET, MEALWORM, LOCUST,
    MOUSE, RAT, CHICKEN, RABBIT
}
