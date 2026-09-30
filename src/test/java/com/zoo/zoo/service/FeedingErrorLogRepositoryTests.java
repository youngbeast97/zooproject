package com.zoo.zoo.service;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import com.zoo.zoo.repository.feedingerror.FeedingErrorLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// HINT: @DataJpaTest starts a real JPA context on in-memory H2, so it validates entity mappings
// HINT: and derived query names - if it fails at context startup, read the root cause carefully.
@DataJpaTest
class FeedingErrorLogRepositoryTests {

    @Autowired
    private FeedingErrorLogRepository repository;

    private FeedingErrorLog log(Long animalId, Long employeeId, LocalDateTime when, boolean resolved) {
        FeedingErrorLog log = new FeedingErrorLog();
        log.setAnimalId(animalId);
        log.setEmployeeId(employeeId);
        log.setErrorMessage("ERR");
        log.setAttemptedFood("MOUSE");
        log.setTimestamp(when);
        log.setResolved(resolved);
        return repository.save(log);
    }

    @Test
    void shouldReturnAnimalHistoryNewestFirst() {
        LocalDateTime now = LocalDateTime.now();
        FeedingErrorLog older = log(1L, 10L, now.minusDays(2), false);
        FeedingErrorLog newer = log(1L, 10L, now.minusHours(1), false);
        log(2L, 10L, now, false);

        List<FeedingErrorLog> history = repository.findByAnimalIdOrderByTimestampDesc(1L);

        assertEquals(List.of(newer.getId(), older.getId()), history.stream().map(FeedingErrorLog::getId).toList());
    }

    @Test
    void shouldCountOnlyUnresolvedErrorsOfEmployee() {
        LocalDateTime now = LocalDateTime.now();
        log(1L, 10L, now, false);
        log(2L, 10L, now, true);
        log(3L, 11L, now, false);

        assertEquals(1, repository.countByEmployeeIdAndResolvedFalse(10L));
    }

    @Test
    void shouldFindStaleUnresolvedErrors() {
        LocalDateTime now = LocalDateTime.now();
        log(1L, 10L, now.minusDays(5), false);
        log(1L, 10L, now.minusDays(5), true);
        log(1L, 10L, now, false);

        assertEquals(1, repository.findByResolvedFalseAndTimestampBefore(now.minusDays(1)).size());
    }
}
