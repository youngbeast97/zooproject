package com.zoo.zoo.service.humidity;

import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.exceptions.humidity.HumidityRefillException;
import com.zoo.zoo.model.animal.Animal;
import com.zoo.zoo.repository.animal.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class HumidityService {
    private final AnimalRepository animalRepository;

    @Transactional
    public void refillHumidity(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalWithIDNotFoundException("Animal not found"));

        if (animal.calculateCurrentHumidity() >= 100.0) {
            throw new HumidityRefillException("Humidity is already 100%");
        }
        animal.setLastHumidityRefillDate(LocalDate.now());
        animalRepository.save(animal);
    }
}
