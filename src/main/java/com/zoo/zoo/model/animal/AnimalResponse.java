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
    // HINT: New read-only fields computed by AnimalMapper for the keepers' dashboard.
    // HINT: Keep JSON naming/format consistent with the other date/number fields of this DTO,
    // HINT: and make sure every field declared here is actually filled in AnimalMapper.fillCommonFields.
    private Boolean readyForFeeding;
    private String humidityStatus;
}
