package com.zoo.zoo.model.feeding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "feeding_error_logs", indexes = {
        @Index(name = "idx_feeding_error_employee", columnList = "employeeId")
})
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
    // HINT: Renamed from "timestamp" - it is a reserved word in several SQL dialects (H2, Oracle)
    // HINT: and broke the H2-based tests. Every reference to the old name has to follow.
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

}
