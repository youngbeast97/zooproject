package com.zoo.zoo.exceptions.feeding;

public class IncorrectFoodMatchedToAnimal extends FeedingFailedException{
    public IncorrectFoodMatchedToAnimal(String message) {
        super(message);
    }
}
