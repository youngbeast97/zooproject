package com.zoo.zoo.model.animal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("VENOMOUS_REPTILE")
@Getter
@Setter
@NoArgsConstructor

public class VenomousReptile extends Animal{
    @Min(value = 1, message = "Weight must be positive")
    private Integer weightInGrams;
    @Override
    public boolean canBeFed(LocalDate currentDate) {
        if(getLastFeedingDate()==null)return true;
        return !currentDate.isBefore(getLastFeedingDate().plusMonths(1));
    }

}
