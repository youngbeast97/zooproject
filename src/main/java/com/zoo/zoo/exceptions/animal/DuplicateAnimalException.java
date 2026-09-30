package com.zoo.zoo.exceptions.animal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateAnimalException extends RuntimeException {

    public DuplicateAnimalException(String message) {
        super(message);
    }
}
