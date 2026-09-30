package com.zoo.zoo.service;

import com.zoo.zoo.model.animal.AnimalRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private AnimalRequest validRequest() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Tuptus");
        request.setSpecies("Ptasznik");
        request.setLastFeedingDate(LocalDate.now().minusDays(3));
        return request;
    }

    @Test
    void shouldAcceptValidRequest() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    }

    @Test
    void shouldRejectSingleLetterName() {
        AnimalRequest request = validRequest();
        request.setName("X");

        Set<ConstraintViolation<AnimalRequest>> violations = validator.validate(request);

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

        assertEquals(1, violations.size());
        assertEquals("lastFeedingDate", violations.iterator().next().getPropertyPath().toString());
    }
}
