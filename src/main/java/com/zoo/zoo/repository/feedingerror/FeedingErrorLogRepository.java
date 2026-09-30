package com.zoo.zoo.repository.feedingerror;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedingErrorLogRepository  extends JpaRepository<FeedingErrorLog,Long> {

    // Last 20 feeding mistakes of an employee, newest first (HR review screen).
    List<FeedingErrorLog> findTop20ByEmployeeIdOrderByOccurredAtDesc(Long employeeId);
}
