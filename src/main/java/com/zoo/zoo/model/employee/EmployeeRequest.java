package com.zoo.zoo.model.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeRequest {
    @NotBlank(message="Name cannot be empty")
    private String name;
    @NotNull(message = "Employee type must be provided")
    private EmployeeType employeeType;
    @NotBlank(message = "Department is required")
    private String department;

}
