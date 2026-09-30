package com.zoo.zoo.model.animal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimalResponse {
    private Long id;
    private String name;
    private String species;
    private Double humidity;
    private LocalDate lastFeedingDate;
    private String type;
    private String lightStatus;
    // HINT: Computed field - null when the animal has never been fed.
    private Integer daysSinceLastFeeding;
}
