package com.zoo.zoo.model.animal;

import com.zoo.zoo.model.feeding.FoodCategory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("SMALL_REPTILE")
@Getter
@Setter
@NoArgsConstructor

public class SmallReptile extends Animal {
    @Override
    public boolean canBeFed(LocalDate today) {
        if (getLastFeedingDate() == null) return true;
        return !getLastFeedingDate().plusDays(7)
                .isAfter(today);
    }

    @Override
    public FoodCategory getDiet() {
        return FoodCategory.INSECT;
    }
}
