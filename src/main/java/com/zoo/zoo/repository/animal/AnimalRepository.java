package com.zoo.zoo.repository.animal;

import com.zoo.zoo.model.animal.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByNameContainingIgnoreCase(String name);

    @Query("SELECT a FROM Spider a")
    List<Animal> findAllSpiders();
    @Query("SELECT a FROM VenomousReptile a")
    List<Animal> findAllVenomous();

}
