package com.zoo.zoo.model.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Species cannot be empty")
    @Size(max = 60, message = "Species must be at most 60 characters")
    // HINT: Species names are copied from the vet registry: letters (incl. Polish ones) and spaces only.
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Species may contain only letters and spaces")
    @NotBlank(message = "Species cannot be empty")
    @Size(max = 60, message = "Species must be at most 60 characters")
    private String species;
    private boolean requiresLight;
    @PastOrPresent(message = "Last feeding date cannot be in the future")
    private LocalDate lastFeedingDate;
}
