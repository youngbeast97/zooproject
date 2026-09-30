package com.zoo.zoo.service.animal;
import com.zoo.zoo.exceptions.animal.*;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.repository.animal.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalMapper animalMapper;

    @Transactional
    public List<AnimalResponse> getAllAnimals() {
        List<Animal> animals = animalRepository.findAll();
        return animalMapper.toResponseList(animals);
    }
    @Transactional
    public AnimalResponse getById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new AnimalWithIDNotFoundException("Not found with id: " + id));
        return animalMapper.territoryToResponse(animal);
    }

    public List<AnimalResponse> getSpiders() {
        return animalMapper.toResponseList(animalRepository.findAllSpiders());
    }

    public List<AnimalResponse> getVenomous() {
        return animalMapper.toResponseList(animalRepository.findAllVenomous());
    }

    public List<AnimalResponse> findByName(String name) {
        return animalMapper.toResponseList(animalRepository.findByNameContainingIgnoreCase(name));
    }

    // HINT: Every create* method follows the same phases: validate request -> map -> save -> map response.
    // HINT: Request validation must happen before anything touches the repository.
    @Transactional
    public AnimalResponse createSpider(AnimalRequest request) {
        validateFeedingDate(request);
        Spider spider = animalMapper.toSpider(request);
        return animalMapper.territoryToResponse(animalRepository.save(spider));
    }

    @Transactional
    public AnimalResponse createSmallReptile(AnimalRequest request) {
        validateFeedingDate(request);
        SmallReptile reptile = animalMapper.toSmallReptile(request);
        return animalMapper.territoryToResponse(animalRepository.save(reptile));
    }

    @Transactional
    public AnimalWithWeightResponse createBigReptile(AnimalWithWeightRequest request) {
        validateFeedingDate(request);
        BigReptile reptile = animalMapper.toBigReptile(request);
        return toWeightResponse(animalRepository.save(reptile), "BigReptile");
    }

    @Transactional
    public AnimalWithWeightResponse createVenomousReptile(AnimalWithWeightRequest request) {
        validateFeedingDate(request);
        VenomousReptile reptile = animalMapper.toVenomousReptile(request);
        return toWeightResponse(animalRepository.save(reptile), "VenomousReptile");
    }

    private AnimalWithWeightResponse toWeightResponse(Animal saved, String label) {
        if (animalMapper.territoryToResponse(saved) instanceof AnimalWithWeightResponse weightResponse) {
            return weightResponse;
        }
        throw new WeightRequiredForBigReptileException("Mapping error: Expected weight response for " + label);
    }

    private void validateFeedingDate(AnimalRequest request) {
        LocalDate lastFeeding = request.getLastFeedingDate();
        if (lastFeeding != null && lastFeeding.isBefore(LocalDate.now().minusYears(1))) {
            throw new InvalidFeedingDateException("Last feeding date " + lastFeeding + " is more than a year ago - check the data");
        }
    }



    public void delete(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new AnimalWithIDNotFoundException("Cannot be deleted, animal with ID: " + id + " doesn't exists");
        }
        animalRepository.deleteById(id);
    }
}
