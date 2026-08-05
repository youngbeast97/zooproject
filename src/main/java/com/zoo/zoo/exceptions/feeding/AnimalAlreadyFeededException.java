package com.zoo.zoo.exceptions.feeding;

public class AnimalAlreadyFeededException extends FeedingFailedException{
    public AnimalAlreadyFeededException(String message) {
        super(message);
    }
}
