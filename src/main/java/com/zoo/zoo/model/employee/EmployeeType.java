package com.zoo.zoo.model.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EmployeeType {
    // HINT: isAtLeast() below derives privileges from something implicit in this declaration.
    // HINT: Before adding or moving constants, work out what each role may do (see FeedingServiceTests
    // HINT: and AnimalFeedingRulesTests) and whether the implicit rule still matches the business rules.
    STUDENT, //spiders,small reptiles
    EXPERIENCED, //+big reptiles
    BOSS; //+venomous

    // Roles are declared from the least to the most privileged one.
    public boolean isAtLeast(EmployeeType required) {
        return this.ordinal() >= required.ordinal();
    }
}
