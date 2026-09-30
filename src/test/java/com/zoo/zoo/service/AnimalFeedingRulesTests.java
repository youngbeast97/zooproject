package com.zoo.zoo.service;

import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.model.employee.EmployeeType;
import com.zoo.zoo.model.feeding.FoodCategory;
import com.zoo.zoo.model.feeding.FoodType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

// HINT: These tests describe the domain rules the refactor moved into the model.
// HINT: When master added roles/food types/ranges, decide which of these expectations must change and which must hold.
class AnimalFeedingRulesTests {

    @Test
    void insectEatersShouldRequireInsects() {
        assertEquals(FoodCategory.INSECT, new Spider().getDiet());
        assertEquals(FoodCategory.INSECT, new SmallReptile().getDiet());
    }

    @Test
    void reptilesShouldRequireMeat() {
        assertEquals(FoodCategory.MEAT, new BigReptile().getDiet());
        assertEquals(FoodCategory.MEAT, new VenomousReptile().getDiet());
    }

    @Test
    void everyFoodTypeShouldHaveCategory() {
        Arrays.stream(FoodType.values()).forEach(food -> assertNotNull(food.getCategory(), food.name()));
    }

    @Test
    void caretakerLevelShouldGrowWithDanger() {
        assertEquals(EmployeeType.STUDENT, new Spider().getMinimumCaretakerLevel());
        assertEquals(EmployeeType.STUDENT, new SmallReptile().getMinimumCaretakerLevel());
        assertEquals(EmployeeType.EXPERIENCED, new BigReptile().getMinimumCaretakerLevel());
        assertEquals(EmployeeType.BOSS, new VenomousReptile().getMinimumCaretakerLevel());
    }

    @Test
    void bossShouldBeAllowedEverythingAnyOtherRoleIsAllowed() {
        for (EmployeeType role : EmployeeType.values()) {
            assertTrue(EmployeeType.BOSS.isAtLeast(role), "BOSS should be at least " + role);
        }
    }

    @Test
    void studentShouldNotCountAsExperienced() {
        assertFalse(EmployeeType.STUDENT.isAtLeast(EmployeeType.EXPERIENCED));
        assertTrue(EmployeeType.EXPERIENCED.isAtLeast(EmployeeType.STUDENT));
    }

    @Test
    void bodyWeightShouldBeZeroForAnimalsWithoutWeight() {
        assertEquals(0, new Spider().getBodyWeightInGrams());
        VenomousReptile cobra = new VenomousReptile();
        assertEquals(0, cobra.getBodyWeightInGrams());
        cobra.setWeightInGrams(1200);
        assertEquals(1200, cobra.getBodyWeightInGrams());
    }
}
