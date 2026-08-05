package com.zoo.zoo.model.feeding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="feeding_error_logs")
@Getter
@Setter

public class FeedingErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long animalId;
    private Long employeeId;
    private String errorMessage;
    private String attemptedFood;
    private LocalDateTime timestamp;

}
