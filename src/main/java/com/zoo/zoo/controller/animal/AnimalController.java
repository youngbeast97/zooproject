package com.zoo.zoo.controller.animal;

import com.zoo.zoo.model.animal.AnimalRequest;
import com.zoo.zoo.model.animal.AnimalResponse;
import com.zoo.zoo.model.animal.AnimalWithWeightRequest;
import com.zoo.zoo.model.animal.AnimalWithWeightResponse;
import com.zoo.zoo.model.feeding.FeedingRequest;
import com.zoo.zoo.service.animal.AnimalService;
import com.zoo.zoo.service.feeding.FeedingService;
import com.zoo.zoo.service.humidity.HumidityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
    private final HumidityService HumidityService;
    private final FeedingService feedingService;

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> getAllAnimals() {
        return ResponseEntity.ok(animalService.getAllAnimals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getAnimalById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getById(id));
    }

    @GetMapping("/spiders")
    public ResponseEntity<List<AnimalResponse>> getAllSpiders() {
        return ResponseEntity.ok(animalService.getSpiders());
    }

    @GetMapping("/venomous")
    public ResponseEntity<List<AnimalResponse>> getAllVenomous() {
        return ResponseEntity.ok(animalService.getVenomous());
    }

    // HINT: Dashboard endpoints live next to the other filtered lists (/spiders, /venomous).
    // HINT: If more filtered lists were added here in the meantime, check they don't duplicate each other.
    @GetMapping("/hungry")
    public ResponseEntity<List<AnimalResponse>> getHungryAnimals() {
        return ResponseEntity.ok(animalService.getHungryAnimals());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnimalResponse>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(animalService.findByName(name));
    }

    @PostMapping("/spider")
    public ResponseEntity<AnimalResponse> createSpider(@Valid @NotNull @RequestBody AnimalRequest request) {
        return ResponseEntity.status(201).body(animalService.createSpider(request));
    }


    @PostMapping("/small-reptile")
    public ResponseEntity<AnimalResponse> createSmallReptile(@Valid @RequestBody AnimalRequest request) {
        return ResponseEntity.status(201).body(animalService.createSmallReptile(request));
    }

    @PostMapping("/big-reptile")
    public ResponseEntity<AnimalWithWeightResponse> createBigReptile(@Valid @RequestBody AnimalWithWeightRequest request) {
        return ResponseEntity.status(201).body(animalService.createBigReptile(request));
    }

    @PostMapping("/venomous-reptile")
    public ResponseEntity<AnimalWithWeightResponse> createVenomousReptile(@Valid @RequestBody AnimalWithWeightRequest request) {
        return ResponseEntity.status(201).body(animalService.createVenomousReptile(request));
    }

    @PatchMapping("/{id}/refill-humidity")
    public ResponseEntity<Void> refillHumidity(@PathVariable Long id) {
        HumidityService.refillHumidity(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/feed-process")
    public ResponseEntity<AnimalResponse> feedAnimal(
            @PathVariable Long id,
            @RequestBody FeedingRequest request) {
        return ResponseEntity.ok(feedingService.feedAnimal(id, request));
    }
}