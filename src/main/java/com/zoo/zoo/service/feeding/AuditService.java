package com.zoo.zoo.service.feeding;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import com.zoo.zoo.repository.feedingerror.FeedingErrorLogRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final FeedingErrorLogRepository errorRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) //require new bylo potrzebne zeby mimo rollbacku zapisaly sie dane z tego nieprawidlowego karmienia
    public void logError(Long animalId, Long employeeId, String message, String food) {
        FeedingErrorLog log = new FeedingErrorLog();
        log.setAnimalId(animalId);
        log.setEmployeeId(employeeId);
        log.setErrorMessage(message);
        log.setAttemptedFood(food);
        log.setOccurredAt(LocalDateTime.now());
        errorRepository.save(log);
    }
}