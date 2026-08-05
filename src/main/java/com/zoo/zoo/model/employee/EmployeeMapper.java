package com.zoo.zoo.model.employee;

import org.springframework.stereotype.Component;

@Component

public class EmployeeMapper {
    public Employee toEntity(EmployeeRequest request) {
        if (request == null) return null;
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmployeeType(request.getEmployeeType());

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) return null;

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmployeeType(employee.getEmployeeType());
        return response;

    }
}
