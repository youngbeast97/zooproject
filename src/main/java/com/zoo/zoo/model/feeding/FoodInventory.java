package com.zoo.zoo.model.feeding;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="food_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private FoodType foodType;

    private Integer currentQuantity;
    private Integer maxQuantity;
    private LocalDate lastRestockDate;
}

