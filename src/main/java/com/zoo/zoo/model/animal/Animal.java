package com.zoo.zoo.model.animal;

import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.model.feeding.FoodCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "animals")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "animal_type")
@Getter @Setter @NoArgsConstructor
public abstract class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String species;
    private String name;
    private Double humidity = 100.0;
    private LocalDate lastHumidityRefillDate = LocalDate.now();
    private LocalDate lastFeedingDate;
    private boolean isRequiresLight=false;//domyslnie zwierzak nie potrzebuje lampy grzewczej
    public abstract boolean canBeFed(LocalDate currentDate);

    // HINT: Feeding rules now live in the domain model and are overridden per species.
    // HINT: FeedingService only asks the animal - it should not need instanceof checks any more.
    // HINT: Check Spider, SmallReptile, BigReptile and VenomousReptile for the overrides.
    public abstract FoodCategory getDiet();

    public EmployeeType getMinimumCaretakerLevel() {
        return EmployeeType.STUDENT;
    }

    /** Body weight used for portion checks; 0 means "portion size is not checked". */
    public int getBodyWeightInGrams() {
        return 0;
    }

    public double getMinFoodRatio() {
        return 0.08;
    }

    public double getMaxFoodRatio() {
        return 0.15;
    }

    public Double calculateCurrentHumidity(){
        if(lastHumidityRefillDate==null)return 0.0;
        long daysPassed= ChronoUnit.DAYS.between(lastHumidityRefillDate,LocalDate.now());
        double currentHumidity =100.0-daysPassed;
        return Math.max(0.0,currentHumidity);
    }

}

