package com.zoo.zoo.model.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    // HINT: Enclosure labels are printed on a fixed-width plate - long names get cut off.
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;
    // HINT: Species names are copied from the vet registry: letters (incl. Polish ones) and spaces only.
    @NotBlank(message = "Species cannot be empty")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Species may contain only letters and spaces")
    private String species;
    private boolean requiresLight;
    private LocalDate lastFeedingDate;
}
