package com.zoo.zoo.exceptions.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeWithIDNotFoundException extends RuntimeException{

    public EmployeeWithIDNotFoundException(String message) {
        super(message);
    }
}
