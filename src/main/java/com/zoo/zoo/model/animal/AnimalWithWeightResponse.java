package com.zoo.zoo.model.animal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AnimalWithWeightResponse extends AnimalResponse {
    private Integer WeightInGrams;

}
