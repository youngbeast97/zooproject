package com.zoo.zoo.model.employee;

import jakarta.validation.constraints.NotBlank;
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
    private EmployeeType employeeType;

}
