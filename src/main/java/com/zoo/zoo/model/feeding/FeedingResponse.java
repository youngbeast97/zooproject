package com.zoo.zoo.model.feeding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// HINT: New API contract for a feeding: a summary of what was actually consumed, instead of the animal itself.
// HINT: If the request contract gained new fields in the meantime, ask yourself whether the client
// HINT: needs to see them echoed here (and update the builder call in FeedingService accordingly).
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedingResponse {
    private Long animalId;
    private String animalName;
    private Long fedByEmployeeId;
    private FoodType foodType;
    private Integer foodWeightInGrams;
    private Integer remainingStock;
    private LocalDateTime fedAt;
}
