package com.zoo.zoo.model.feeding;

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
    private Integer foodWeightInGrams;


}
