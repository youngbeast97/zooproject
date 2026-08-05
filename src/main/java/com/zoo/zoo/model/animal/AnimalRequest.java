package com.zoo.zoo.model.animal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AnimalRequest {
    @NotBlank(message="Name cannot be empty")
    private String name;
    @NotBlank(message = "Spiece cannot be empty")
    private String species;
    private boolean requiresLight;
    private LocalDate lastFeedingDate;
}
