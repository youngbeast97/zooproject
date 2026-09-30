package com.zoo.zoo.service;

import com.zoo.zoo.model.animal.AnimalRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// HINT: Unit tests for the Bean Validation rules on AnimalRequest - keep them in sync with the annotations.
class AnimalRequestValidationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private AnimalRequest request(String name, String species) {
        AnimalRequest request = new AnimalRequest();
        request.setName(name);
        request.setSpecies(species);
        return request;
    }

    @Test
    void shouldAcceptPolishSpeciesName() {
        assertTrue(validator.validate(request("Tuptuś", "Ptasznik różowy")).isEmpty());
    }

    @Test
    void shouldRejectNameLongerThanThirtyCharacters() {
        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request("A".repeat(31), "Ptasznik"));

        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRejectSpeciesWithDigits() {
        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request("Tuptuś", "Ptasznik 2"));

        assertEquals(1, violations.size());
        assertEquals("species", violations.iterator().next().getPropertyPath().toString());
    }
}
