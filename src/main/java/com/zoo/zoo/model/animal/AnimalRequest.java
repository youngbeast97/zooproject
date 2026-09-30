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
    // HINT: The keeper app shows names on enclosure labels; think about what a sensible
    // HINT: length range is and whether a one-letter name should ever be accepted.
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
    private String name;
    @NotBlank(message = "Species cannot be empty")
    @Size(max = 60, message = "Species must be at most 60 characters")
    private String species;
    private boolean requiresLight;
    @PastOrPresent(message = "Last feeding date cannot be in the future")
    private LocalDate lastFeedingDate;
}
