package com.zoo.zoo.model.animal;

import com.zoo.zoo.model.feeding.FoodCategory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@DiscriminatorValue("SPIDER")
@Getter @Setter @NoArgsConstructor
public class Spider extends Animal {

    @Override
    public boolean canBeFed(LocalDate currentDate) {
        if (getLastFeedingDate() == null) return true;
        return !currentDate.isBefore(getLastFeedingDate().plusWeeks(2));
    }

    @Override
    public FoodCategory getDiet() {
        return FoodCategory.INSECT;
    }

}