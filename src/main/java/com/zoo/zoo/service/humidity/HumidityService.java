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

        // HINT: One "today" is captured per request and used for both the check and the new refill date,
        // HINT: so both decisions are based on the same day even around midnight.
        LocalDate today = LocalDate.now();
        if (animal.calculateCurrentHumidity(today) >= 100.0) {
            throw new HumidityRefillException("Humidity is already 100%");
        }
        animal.setLastHumidityRefillDate(today);
        animalRepository.save(animal);
    }
}
