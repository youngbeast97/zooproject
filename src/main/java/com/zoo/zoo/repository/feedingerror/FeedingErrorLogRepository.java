package com.zoo.zoo.repository.feedingerror;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedingErrorLogRepository  extends JpaRepository<FeedingErrorLog,Long> {

    // HINT: Spring Data derives the SQL from the method NAME - every property in the name
    // HINT: (AnimalId, Timestamp, Resolved, ...) must exist on FeedingErrorLog with exactly that name.
    // HINT: If an entity field is renamed, these names are not checked by the compiler, only at startup.
    List<FeedingErrorLog> findByAnimalIdOrderByTimestampDesc(Long animalId);

    long countByEmployeeIdAndResolvedFalse(Long employeeId);

    List<FeedingErrorLog> findByResolvedFalseAndTimestampBefore(LocalDateTime threshold);
}
