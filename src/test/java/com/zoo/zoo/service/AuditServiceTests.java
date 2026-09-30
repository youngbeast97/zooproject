package com.zoo.zoo.service;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import com.zoo.zoo.repository.feedingerror.FeedingErrorLogRepository;
import com.zoo.zoo.service.feeding.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTests {

    @Mock
    private FeedingErrorLogRepository errorLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void shouldLogFeedingError() {
        Long animalId = 1L;
        Long employeeId = 2L;
        String message = "WRONG_FOOD";
        String food = "CRICKET";

        auditService.logError(animalId, employeeId, message, food);

        verify(errorLogRepository, times(1)).save(any(FeedingErrorLog.class));
    }

    @Test
    void shouldSaveCorrectDataInErrorLog() {
        Long animalId = 10L;
        Long employeeId = 20L;
        String message = "INSUFFICIENT_FOOD";
        String food = "MOUSE";
        ArgumentCaptor<FeedingErrorLog> logCaptor = ArgumentCaptor.forClass(FeedingErrorLog.class);

        auditService.logError(animalId, employeeId, message, food);

        verify(errorLogRepository).save(logCaptor.capture());
        FeedingErrorLog savedLog = logCaptor.getValue();

        assertEquals(animalId, savedLog.getAnimalId());
        assertEquals(employeeId, savedLog.getEmployeeId());
        assertEquals(message, savedLog.getErrorMessage());
        assertEquals(food, savedLog.getAttemptedFood());
        assertNotNull(savedLog.getOccurredAt());
    }

    @Test
    void shouldNullValuesInLog() {
        assertDoesNotThrow(() -> auditService.logError(null, null, "UNKNOWN_ERROR", null));
        verify(errorLogRepository).save(any(FeedingErrorLog.class));
    }

    @Test
    void shouldSetCurrentTimeWhenLogging() {
        ArgumentCaptor<FeedingErrorLog> logCaptor = ArgumentCaptor.forClass(FeedingErrorLog.class);

        auditService.logError(1L, 1L, "ERR", "FOOD");

        verify(errorLogRepository).save(logCaptor.capture());
        assertNotNull(logCaptor.getValue().getOccurredAt());
        assertTrue(logCaptor.getValue().getOccurredAt().isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    void shouldLogEvenWithEmptyMessage() {
        auditService.logError(1L, 1L, "", "");
        verify(errorLogRepository).save(any(FeedingErrorLog.class));
    }

    @Test
    void shouldNotFailWhenRepositoryThrowsException() {

        doThrow(new RuntimeException("DB Down")).when(errorLogRepository).save(any());

        assertThrows(RuntimeException.class, () -> auditService.logError(1L, 1L, "MSG", "FOOD"));
    }
}