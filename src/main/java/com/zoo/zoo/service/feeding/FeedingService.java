package com.zoo.zoo.service.feeding;

import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.exceptions.feeding.*;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.model.employee.Employee;
import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.model.feeding.FeedingRequest;
import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.model.feeding.FoodType;
import com.zoo.zoo.repository.animal.AnimalRepository;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedingService {
    private final AnimalRepository animalRepository;
    private final EmployeeRepository employeeRepository;
    private final FoodInventoryRepository inventoryRepository;
    private final AuditService auditService;
    private final AnimalMapper animalMapper;

    @Transactional
    public AnimalResponse feedAnimal(Long animalId, FeedingRequest request) {
        try {
            // 1. PODSTAWA PODSTAW CZY ZWIERZE KTORE CHCESZ NAKARMIC (O DANYM ID) WGL ISTNIEJE
            Animal animal = animalRepository.findById(animalId)
                    .orElseThrow(() -> new AnimalWithIDNotFoundException("ANIMAL_NOT_FOUND"));
            Employee employee = findEmployeeById(request); // CTRL+ALT+M to wydzielenie do osobnej metody

            // SPRAWDANIE CZY MAMY WGL ZARCIE DLA ZWIERZAKA
            FoodInventory inventory = inventoryRepository.findByFoodType(request.getFoodType())
                    .orElseThrow(() -> new FoodTypeNotFoundException("FOOD_NOT_IN_WAREHOUSE"));

            // HINT: Stock is counted in portions - a feeding needs as many units as it has portions.
            if (inventory.getCurrentQuantity() < request.getPortions()) {
                throw new FoodTypeNotFoundException(String.format("OUT_OF_STOCK: %s (requested %d, available %d)",
                        request.getFoodType(), request.getPortions(), inventory.getCurrentQuantity()));
            }

            // SPRAWDZANIE CZY ODPOWIEDNI PRACOWNIK SIE ZABIERA ZA ROBOTE
            validatePermissions(employee, animal);

            // SPRAWDZANKO CZY MINAŁ ODPOWIEDNI OKRES CZASU OD OSTATNIEGO KARMIENIA
            if (!animal.canBeFed(LocalDate.now())) {
                throw new AnimalAlreadyFeededException("TIMING_ERROR: Animal is still full!");
            }

            // SPRAWDZANIE CZY MASA KARMÓWKI JEST W ZAKRESIE 8%-15% MASY ZWIERZA
            validateDietAndWeight(animal, request);

            //  JEŚLI DOSZLIŚMY TUTAJ, WSZYSTKO JEST CACY
            inventory.setCurrentQuantity(inventory.getCurrentQuantity() - request.getPortions());
            animal.setLastFeedingDate(LocalDate.now());

            return animalMapper.territoryToResponse(animalRepository.save(animal));

        } catch (FeedingFailedException e) {
            log.error("Feeding Failed {}", e.getMessage() );
            //NAWIAZANIE DO TEGO ZE JESLI SIE NIE UDA KARMIENIE Z JAKIEGOS POWODU TO BEDZIE TO W ODPOWIEDNIEJ TABELI ZAPISANE
            //NP JESLI KTOS KARMI WĘŻA ŚWIERSZCZAMI TO MAMY DOWÓD KIEDY I JAK I MOŻNA GO WYPIERDOLIC Z ROBOTY :)
            auditService.logError(animalId, request.getEmployeeId(), e.getMessage(),
                    request.getFoodType() != null ? request.getFoodType().toString() : "!");

            throw e;
        }
    }

    private Employee findEmployeeById(FeedingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeWithIDNotFoundException("EMPLOYEE_NOT_FOUND"));
        return employee;
    }

    private void validatePermissions(Employee employee, Animal animal) {
        EmployeeType role = employee.getEmployeeType();

        if (animal instanceof VenomousReptile && role != EmployeeType.BOSS) {
            throw new IncorrectEmployeeFeedingException("UNAUTHORIZED: Only BOSS can feed venomous reptiles!");
        }
        if (animal instanceof BigReptile && (role == EmployeeType.STUDENT || role == EmployeeType.INTERN)) {
            throw new IncorrectEmployeeFeedingException("UNAUTHORIZED: Students cannot feed big reptiles!");
        }
        // HINT: Interns may feed spiders only - not even small reptiles.
        if (role == EmployeeType.INTERN && !(animal instanceof Spider)) {
            throw new IncorrectEmployeeFeedingException("UNAUTHORIZED: Interns may only feed spiders!");
        }
    }
    private void validateDietAndWeight(Animal animal, FeedingRequest request) {
        List<FoodType> insects = List.of(FoodType.COCKROACH, FoodType.CRICKET, FoodType.MEALWORM, FoodType.LOCUST);
        List<FoodType> meat = List.of(FoodType.MOUSE, FoodType.RAT, FoodType.CHICKEN, FoodType.RABBIT);

        if (animal instanceof Spider || animal instanceof SmallReptile) {
            if (!insects.contains(request.getFoodType())) {
                throw new IncorrectFoodMatchedToAnimal("This animal eats only insects!");
            }
            return;
        }

        if (!meat.contains(request.getFoodType())) {
            throw new IncorrectFoodMatchedToAnimal("Large/Venomous reptiles require meat not worms!");
        }

        // HINT: The body-mass rule applies to the TOTAL amount eaten in one feeding (all portions together).
        // HINT: Vet recommendation 2026-09: venomous reptiles get 5-12%, all other weighed animals keep 8-15%.
        Integer animalWeight = getAnimalWeight(animal);
        if (animalWeight > 0) {
            boolean venomous = animal instanceof VenomousReptile;
            double minRatio = venomous ? 0.05 : 0.08;
            double maxRatio = venomous ? 0.12 : 0.15;
            double min = animalWeight * minRatio;
            double max = animalWeight * maxRatio;
            int totalWeight = request.getFoodWeightInGrams() * request.getPortions();

            if (totalWeight < min || totalWeight > max) {
                throw new IncorrectWeightOfFoodException(String.format(
                        "Food (%d x %dg = %dg) must be %.0f-%.0f%% of animal mass (%dg). Range: [%.0fg - %.0fg]",
                        request.getPortions(), request.getFoodWeightInGrams(), totalWeight,
                        minRatio * 100, maxRatio * 100, animalWeight, min, max));
            }
        }
    }

    private Integer getAnimalWeight(Animal animal) {
        if (animal instanceof BigReptile br) {
            return br.getWeightInGrams();
        }
        if (animal instanceof VenomousReptile vr){
            return vr.getWeightInGrams();
        }
        return 0;
    }
}
