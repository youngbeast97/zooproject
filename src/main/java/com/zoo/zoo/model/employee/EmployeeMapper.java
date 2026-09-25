package com.zoo.zoo.model.employee;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component

public class EmployeeMapper {
    public Employee toEntity(EmployeeRequest request) {
        if (request == null) return null;
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmployeeType(request.getEmployeeType());
        employee.setDepartment(request.getDepartment());
        employee.setHireDate(LocalDate.now());

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) return null;

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmployeeType(employee.getEmployeeType());
        response.setDepartment(employee.getDepartment());
        response.setHireDate(employee.getHireDate());
        return response;

    }
}
