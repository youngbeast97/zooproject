package com.zoo.zoo.service.humidity;

import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.exceptions.humidity.HumidityRefillException;
import com.zoo.zoo.model.animal.Animal;
import com.zoo.zoo.model.animal.AnimalMapper;
import com.zoo.zoo.model.animal.AnimalResponse;
import com.zoo.zoo.repository.animal.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class HumidityService {
    private final AnimalRepository animalRepository;
    private final AnimalMapper animalMapper;

    @Transactional
    public void refillHumidity(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalWithIDNotFoundException("Animal not found"));

        // HINT: A refill is pointless (and wastes water) when the terrarium is already at the maximum.
        if (animal.calculateCurrentHumidity() >= Animal.MAX_HUMIDITY) {
            throw new HumidityRefillException("Humidity is already " + Animal.MAX_HUMIDITY + "%");
        }
        animal.setLastHumidityRefillDate(LocalDate.now());
        animalRepository.save(animal);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> findAnimalsNeedingRefill(double threshold) {
        List<Animal> dry = animalRepository.findAll().stream()
                .filter(animal -> animal.calculateCurrentHumidity() < threshold)
                .toList();
        return animalMapper.toResponseList(dry);
    }
}
