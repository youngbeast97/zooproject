package com.zoo.zoo.model.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeResponse {
    private Long id;
    private String name;
    private EmployeeType employeeType;

}
