package com.zoo.zoo.model.animal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AnimalWithWeightRequest extends AnimalRequest {
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be >0")
    @Max(value = 200_000, message = "Weight above 200kg is almost certainly a typo")
    private Integer weightInGrams;

}
