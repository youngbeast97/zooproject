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

    // HINT: The reference date is now passed in by the caller instead of reading the clock here,
    // HINT: so the result is deterministic in tests. Any other change to how humidity is computed
    // HINT: must still work with this signature - search for every caller of this method afterwards.
    public Double calculateCurrentHumidity(LocalDate today) {
        if (lastHumidityRefillDate == null) return 0.0;
        long daysPassed = ChronoUnit.DAYS.between(lastHumidityRefillDate, today);
        double currentHumidity = 100.0 - daysPassed;
        return Math.max(0.0, currentHumidity);
    }

}

