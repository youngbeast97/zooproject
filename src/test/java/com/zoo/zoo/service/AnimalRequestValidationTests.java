package com.zoo.zoo.service;

import com.zoo.zoo.model.animal.AnimalRequest;
import com.zoo.zoo.model.animal.AnimalWithWeightRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// HINT: Unit tests for the Bean Validation rules on AnimalRequest - keep them in sync with the annotations.
// HINT: This test class validates AnimalRequest in isolation (no Spring context).
// HINT: Every assertion about a length/format limit must agree with the annotations in AnimalRequest.
class AnimalRequestValidationTests {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    private Validator validator;

    @AfterAll
    static void closeFactory() {
        factory.close();
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private AnimalRequest validRequest() {
    private AnimalRequest request(String name, String species) {
        AnimalRequest request = new AnimalRequest();
        request.setName("Tuptus");
        request.setSpecies("Ptasznik");
        request.setLastFeedingDate(LocalDate.now().minusDays(3));
        request.setName(name);
        request.setSpecies(species);
        return request;
    }

    @Test
    void shouldAcceptValidRequest() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    void shouldAcceptPolishSpeciesName() {
        assertTrue(validator.validate(request("Tuptuś", "Ptasznik różowy")).isEmpty());
    }

    @Test
    void shouldRejectSingleLetterName() {
        AnimalRequest request = validRequest();
        request.setName("X");

        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request);
    void shouldRejectNameLongerThanThirtyCharacters() {
        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request("A".repeat(31), "Ptasznik"));

        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAcceptNameWithFortyCharacters() {
        AnimalRequest request = validRequest();
        request.setName("A".repeat(40));

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectFeedingDateInTheFuture() {
        AnimalRequest request = validRequest();
        request.setLastFeedingDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request);
    void shouldRejectSpeciesWithDigits() {
        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request("Tuptuś", "Ptasznik 2"));

        assertEquals(1, violations.size());
        assertEquals("lastFeedingDate", violations.iterator().next().getPropertyPath().toString());
        assertEquals("species", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRequireWeightForWeightedAnimals() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        request.setName("Kobra");
        request.setSpecies("Kobra krolewska");

        Set<ConstraintViolation<AnimalWithWeightRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("weightInGrams", violations.iterator().next().getPropertyPath().toString());
    }
}
