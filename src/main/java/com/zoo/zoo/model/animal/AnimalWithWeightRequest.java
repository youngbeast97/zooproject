package com.zoo.zoo.model.animal;

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
    @Positive(message = "Weight must be >0")
    private Integer weightInGrams;

}
