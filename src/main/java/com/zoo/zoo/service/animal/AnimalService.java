package com.zoo.zoo.service.animal;
import com.zoo.zoo.exceptions.animal.*;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.repository.animal.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // HINT: Business rule: two animals of the same species must not share a name (keepers identify them by name).
    // HINT: The check has to run before anything is persisted, in EVERY create* method - including any
    // HINT: validation that other create* changes introduce. Compare with AnimalServiceTests.
    public AnimalResponse createSpider(AnimalRequest request) {
        ensureNameIsUnique(request);
        Spider spider = animalMapper.toSpider(request);
        return animalMapper.territoryToResponse(animalRepository.save(spider));
    }

    public AnimalResponse createSmallReptile(AnimalRequest request) {
        ensureNameIsUnique(request);
        SmallReptile reptile = animalMapper.toSmallReptile(request);
        return animalMapper.territoryToResponse(animalRepository.save(reptile));
    }
    public AnimalWithWeightResponse createBigReptile(AnimalWithWeightRequest request) {
        ensureNameIsUnique(request);
        BigReptile reptile = animalMapper.toBigReptile(request);
        Animal saved = animalRepository.save(reptile);

        if(animalMapper.territoryToResponse(saved)instanceof AnimalWithWeightResponse weightResponse){
            return weightResponse;
        }
        throw new WeightRequiredForBigReptileException("Mapping error: Expected weight response for BigReptile");
    }

    public AnimalWithWeightResponse createVenomousReptile(AnimalWithWeightRequest request) {
        ensureNameIsUnique(request);
        VenomousReptile reptile = animalMapper.toVenomousReptile(request);
        Animal saved = animalRepository.save(reptile);
        if (animalMapper.territoryToResponse(saved) instanceof AnimalWithWeightResponse weightResponse) {
            return weightResponse;
        }
        throw new WeightRequiredForBigReptileException("Mapping error: Expected weight response for VenomousReptile");
    }



    private void ensureNameIsUnique(AnimalRequest request) {
        if (animalRepository.existsByNameIgnoreCaseAndSpeciesIgnoreCase(request.getName(), request.getSpecies())) {
            throw new DuplicateAnimalException(
                    "Animal named '" + request.getName() + "' of species '" + request.getSpecies() + "' already exists");
        }
    }

    public void delete(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new AnimalWithIDNotFoundException("Cannot be deleted, animal with ID: " + id + " doesn't exists");
        }
        animalRepository.deleteById(id);
    }
}
