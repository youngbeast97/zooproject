package com.zoo.zoo.model.feeding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// HINT: Indexes are declared on the @Table annotation - a table can have several, but only one @Table.
// HINT: Keep every index whose query is still used somewhere (look at FeedingErrorLogRepository).
@Entity
@Table(name = "feeding_error_logs", indexes = {
        @Index(name = "idx_feeding_error_animal", columnList = "animalId")
})
@Getter
@Setter

public class FeedingErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long animalId;
    private Long employeeId;
    @Column(length = 500)
    private String errorMessage;
    private String attemptedFood;
    private LocalDateTime timestamp;

    // HINT: Resolution tracking - a supervisor marks an error as handled.
    private boolean resolved = false;
    private LocalDateTime resolvedAt;

}
