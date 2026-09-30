package com.zoo.zoo.service;

import com.zoo.zoo.exceptions.humidity.HumidityRefillException;
import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.model.animal.AnimalMapper;
import com.zoo.zoo.model.animal.AnimalResponse;
import com.zoo.zoo.model.animal.Spider;
import com.zoo.zoo.repository.animal.AnimalRepository;
import com.zoo.zoo.service.humidity.HumidityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HumidityServiceTests {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalMapper animalMapper;

    @InjectMocks
    private HumidityService humidityService;

    @Test
    void shouldRefillHumiditySuccessfully() {
        Spider spider = new Spider();
        spider.setId(1L);
        spider.setLastHumidityRefillDate(LocalDate.now().minusDays(10));
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));

        humidityService.refillHumidity(1L);

        assertEquals(LocalDate.now(), spider.getLastHumidityRefillDate());
        verify(animalRepository).save(spider);
    }

    @Test
    void shouldThrowExceptionWhenHumidityAlreadyFull() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(LocalDate.now());
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));

        assertThrows(HumidityRefillException.class, () -> humidityService.refillHumidity(1L));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void shouldRefillHumidityWhenDateIsNull() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(null);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));

        humidityService.refillHumidity(1L);

        assertEquals(LocalDate.now(), spider.getLastHumidityRefillDate());
        verify(animalRepository).save(spider);
    }

    @Test
    void shouldThrowExceptionWhenAnimalNotFoundForHumidityRefill() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AnimalWithIDNotFoundException.class,
                () -> humidityService.refillHumidity(99L));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void shouldHandleLargeIdInHumidityService() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(LocalDate.now().minusDays(30));
        Long largeId = 999999L;
        when(animalRepository.findById(largeId)).thenReturn(Optional.of(spider));

        humidityService.refillHumidity(largeId);

        verify(animalRepository).save(spider);
    }

    @Test
    void shouldVerifySaveCalledExactlyOnce() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(LocalDate.now().minusDays(5));
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));

        humidityService.refillHumidity(1L);

        verify(animalRepository, times(1)).save(spider);
    }

    @Test
    void shouldCalculateCurrentHumidityCorrectly() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(LocalDate.now().minusDays(10));

        double currentHumidity = spider.calculateCurrentHumidity();
        assertEquals(90.0, currentHumidity);
    }

    @Test
    void shouldNotRefillIfDateIsFromToday() {
        Spider spider = new Spider();
        spider.setLastHumidityRefillDate(LocalDate.now());
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));

        assertThrows(HumidityRefillException.class, () -> humidityService.refillHumidity(1L));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void shouldListOnlyAnimalsBelowHumidityThreshold() {
        Spider dry = new Spider();
        dry.setLastHumidityRefillDate(LocalDate.now().minusDays(70));
        Spider wet = new Spider();
        wet.setLastHumidityRefillDate(LocalDate.now().minusDays(5));
        AnimalResponse dryResponse = new AnimalResponse();

        when(animalRepository.findAll()).thenReturn(List.of(dry, wet));
        when(animalMapper.toResponseList(List.of(dry))).thenReturn(List.of(dryResponse));

        assertEquals(List.of(dryResponse), humidityService.findAnimalsNeedingRefill(50.0));
    }
}
