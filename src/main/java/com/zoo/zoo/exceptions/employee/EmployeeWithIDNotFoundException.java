package com.zoo.zoo.exceptions.employee;

public class EmployeeWithIDNotFoundException extends RuntimeException{

    public EmployeeWithIDNotFoundException(String message) {
        super(message);
    }
}
