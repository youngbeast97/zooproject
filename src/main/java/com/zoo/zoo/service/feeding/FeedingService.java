package com.zoo.zoo.service.feeding;

import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.exceptions.employee.EmployeeWithIDNotFoundException;
import com.zoo.zoo.exceptions.feeding.*;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.model.employee.Employee;
import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.model.feeding.FeedingRequest;
import com.zoo.zoo.model.feeding.FoodCategory;
import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.repository.animal.AnimalRepository;
import com.zoo.zoo.repository.employee.EmployeeRepository;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

            if (inventory.getCurrentQuantity() <= 0) {
                throw new FoodTypeNotFoundException("OUT_OF_STOCK: " + request.getFoodType());
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
            inventory.setCurrentQuantity(inventory.getCurrentQuantity() - 1);
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

    // HINT: REFACTOR - the rules below used to be hard-coded instanceof chains; now the animal decides.
    // HINT: Every rule that exists on the other side of this conflict (roles, food types, portion ranges,
    // HINT: portion counts...) must still be enforced after the merge - either here or in the model classes.
    // HINT: Make a list of the rules from both sides and of the tests in FeedingServiceTests that cover them.
    private void validatePermissions(Employee employee, Animal animal) {
        EmployeeType role = employee.getEmployeeType();
        EmployeeType required = animal.getMinimumCaretakerLevel();

        if (!role.isAtLeast(required)) {
            throw new IncorrectEmployeeFeedingException(String.format(
                    "UNAUTHORIZED: %s cannot feed %s (requires %s)",
                    role, animal.getClass().getSimpleName(), required));
        }
    }

    private void validateDietAndWeight(Animal animal, FeedingRequest request) {
        FoodCategory offered = request.getFoodType().getCategory();
        if (offered != animal.getDiet()) {
            throw new IncorrectFoodMatchedToAnimal(String.format(
                    "%s eats %s, not %s!", animal.getClass().getSimpleName(), animal.getDiet(), request.getFoodType()));
        }

        int animalWeight = animal.getBodyWeightInGrams();
        if (animalWeight > 0) {
            double min = animalWeight * animal.getMinFoodRatio();
            double max = animalWeight * animal.getMaxFoodRatio();

            if (request.getFoodWeightInGrams() < min || request.getFoodWeightInGrams() > max) {
                throw new IncorrectWeightOfFoodException(String.format(
                        "Food (%dg) must be %.0f-%.0f%% of animal mass (%dg). Range: [%.0fg - %.0fg]",
                        request.getFoodWeightInGrams(), animal.getMinFoodRatio() * 100, animal.getMaxFoodRatio() * 100,
                        animalWeight, min, max));
            }
        }
    }
}
