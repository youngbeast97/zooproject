package com.zoo.zoo.model.feeding;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FeedingRequest {
    @NotNull(message = "EMPLOYEE_ID_REQUIRED")
    private Long employeeId;
    @NotNull(message = "FOOD_TYPE_REQUIRED")
    private FoodType foodType;
    // HINT: Weight is validated at the API boundary now; FeedingService still checks the 8-15% rule for reptiles.
    @Positive(message = "FOOD_WEIGHT_MUST_BE_POSITIVE")
    private Integer foodWeightInGrams;


}
