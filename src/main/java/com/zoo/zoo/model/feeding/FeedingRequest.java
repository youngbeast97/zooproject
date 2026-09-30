package com.zoo.zoo.model.feeding;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FeedingRequest {
    @NotNull(message = "EMPLOYEE_ID_REQUIRED")
    private Long employeeId;
    @NotNull(message = "FOOD_TYPE_REQUIRED")
    private FoodType foodType;
    // HINT: Weight of ONE portion. A feeding may consist of several portions of the same food.
    private Integer foodWeightInGrams;
    @Min(value = 1, message = "PORTIONS_MUST_BE_POSITIVE")
    private int portions = 1;


}
