package com.zoo.zoo.model.animal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("BIG_REPTILE")
@Getter
@Setter
@NoArgsConstructor

public class BigReptile extends Animal{
    @Min(value = 1001, message = "Big reptile must weigh more than 1000g")
    private int weightInGrams;

    @Override
    public boolean canBeFed(LocalDate currentDate) {
        if(getLastFeedingDate()==null)return true;
        return !currentDate.isBefore(getLastFeedingDate().plusMonths(1));
    }
}
