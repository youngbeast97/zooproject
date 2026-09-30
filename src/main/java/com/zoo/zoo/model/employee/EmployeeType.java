package com.zoo.zoo.model.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EmployeeType {
    STUDENT, //spiders,small reptiles
    EXPERIENCED, //+big reptiles
    BOSS, //+venomous
    // HINT: New role from the internship programme - the LEAST privileged one: spiders only, always supervised.
    INTERN //spiders only
}
