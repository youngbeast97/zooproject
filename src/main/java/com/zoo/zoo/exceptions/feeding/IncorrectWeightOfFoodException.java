package com.zoo.zoo.exceptions.feeding;

public class IncorrectWeightOfFoodException extends FeedingFailedException{
    public IncorrectWeightOfFoodException(String message) {
        super(message);
    }
}
