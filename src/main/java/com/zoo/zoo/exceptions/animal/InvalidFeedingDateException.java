package com.zoo.zoo.exceptions.animal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFeedingDateException extends RuntimeException {

    public InvalidFeedingDateException(String message) {
        super(message);
    }
}
