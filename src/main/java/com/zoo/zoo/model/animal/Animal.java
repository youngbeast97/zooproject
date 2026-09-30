package com.zoo.zoo.model.animal;

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
    public static final double MAX_HUMIDITY = 100.0;
    public static final double MIN_HUMIDITY = 0.0;
    public static final double DAILY_HUMIDITY_LOSS = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String species;
    private String name;
    private Double humidity = MAX_HUMIDITY;
    private LocalDate lastHumidityRefillDate = LocalDate.now();
    private LocalDate lastFeedingDate;
    private boolean isRequiresLight=false;//domyslnie zwierzak nie potrzebuje lampy grzewczej
    public abstract boolean canBeFed(LocalDate currentDate);

    // HINT: Humidity drops linearly from MAX_HUMIDITY by DAILY_HUMIDITY_LOSS per day since the last refill.
    // HINT: Other classes (HumidityService, AnimalMapper) rely on these constants and on this method.
    public Double calculateCurrentHumidity() {
        if (lastHumidityRefillDate == null) return MIN_HUMIDITY;
        long daysPassed = ChronoUnit.DAYS.between(lastHumidityRefillDate, LocalDate.now());
        double currentHumidity = MAX_HUMIDITY - daysPassed * DAILY_HUMIDITY_LOSS;
        return Math.max(MIN_HUMIDITY, currentHumidity);
    }

}

