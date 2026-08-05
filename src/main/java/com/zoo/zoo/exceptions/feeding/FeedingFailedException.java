package com.zoo.zoo.exceptions.feeding;

public class FeedingFailedException extends RuntimeException {
    public FeedingFailedException(String message) {
        super(message);
    }
}