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

    @Transactional(readOnly = true)
    public List<AnimalResponse> getHungryAnimals() {
        LocalDate today = LocalDate.now();
        List<Animal> hungry = animalRepository.findAll().stream()
                .filter(animal -> animal.canBeFed(today))
                .toList();
        return animalMapper.toResponseList(hungry);
    }

    public List<AnimalResponse> findByName(String name) {
        return animalMapper.toResponseList(animalRepository.findByNameContainingIgnoreCase(name));
    }

    public AnimalResponse createSpider(AnimalRequest request) {
        Spider spider = animalMapper.toSpider(request);
        return animalMapper.territoryToResponse(animalRepository.save(spider));
    }

    public AnimalResponse createSmallReptile(AnimalRequest request) {
        SmallReptile reptile = animalMapper.toSmallReptile(request);
        return animalMapper.territoryToResponse(animalRepository.save(reptile));
    }
    public AnimalWithWeightResponse createBigReptile(AnimalWithWeightRequest request) {
        BigReptile reptile = animalMapper.toBigReptile(request);
        Animal saved = animalRepository.save(reptile);

        if(animalMapper.territoryToResponse(saved)instanceof AnimalWithWeightResponse weightResponse){
            return weightResponse;
        }
        throw new WeightRequiredForBigReptileException("Mapping error: Expected weight response for BigReptile");
    }

    public AnimalWithWeightResponse createVenomousReptile(AnimalWithWeightRequest request) {
        VenomousReptile reptile = animalMapper.toVenomousReptile(request);
        Animal saved = animalRepository.save(reptile);
        if (animalMapper.territoryToResponse(saved) instanceof AnimalWithWeightResponse weightResponse) {
            return weightResponse;
        }
        throw new WeightRequiredForBigReptileException("Mapping error: Expected weight response for VenomousReptile");
    }



    public void delete(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new AnimalWithIDNotFoundException("Cannot be deleted, animal with ID: " + id + " doesn't exists");
        }
        animalRepository.deleteById(id);
    }
}
