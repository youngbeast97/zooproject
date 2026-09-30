package com.zoo.zoo.service;

import com.zoo.zoo.exceptions.animal.AnimalWithIDNotFoundException;
import com.zoo.zoo.exceptions.animal.WeightRequiredForBigReptileException;
import com.zoo.zoo.model.animal.*;
import com.zoo.zoo.repository.animal.AnimalRepository;
import com.zoo.zoo.service.animal.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTests {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalMapper animalMapper;

    @InjectMocks
    private AnimalService animalService;

    private Spider spider;
    private SmallReptile smallReptile;
    private AnimalResponse spiderResponse;
    private AnimalResponse smallReptileResponse;

    @BeforeEach
    void setUp() {
        spider = new Spider();
        spider.setId(1L);
        spider.setName("Tuptuś");

        spiderResponse = new AnimalResponse();
        spiderResponse.setId(1L);
        spiderResponse.setName("Tuptuś");

        smallReptile = new SmallReptile();
        smallReptile.setId(2L);
        smallReptile.setName("Bubuś");

        smallReptileResponse = new AnimalResponse();
        smallReptileResponse.setId(2L);
        smallReptileResponse.setName("Bubuś");
    }


    @Test
    void shouldGetAllAnimals() {
        List<Animal> animals = List.of(spider, smallReptile);
        List<AnimalResponse> responses = List.of(spiderResponse, smallReptileResponse);

        when(animalRepository.findAll()).thenReturn(animals);
        when(animalMapper.toResponseList(animals)).thenReturn(responses);

        List<AnimalResponse> result = animalService.getAllAnimals();

        assertEquals(2, result.size());
        assertEquals(responses, result);
        verify(animalRepository).findAll();
    }

    @Test
    void shouldFindSpiderById() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(spider));
        when(animalMapper.territoryToResponse(spider)).thenReturn(spiderResponse);

        AnimalResponse result = animalService.getById(1L);

        assertEquals(spiderResponse, result);
        verify(animalRepository).findById(1L);
    }

    @Test
    void shouldFindSmallReptileById() {
        when(animalRepository.findById(2L)).thenReturn(Optional.of(smallReptile));
        when(animalMapper.territoryToResponse(smallReptile)).thenReturn(smallReptileResponse);

        AnimalResponse result = animalService.getById(2L);

        assertEquals("Bubuś", result.getName());
        verify(animalRepository).findById(2L);
    }

    @Test
    void shouldGiveBackAllSpiders() {
        List<Animal> spiders = List.of(spider);
        List<AnimalResponse> responses = List.of(spiderResponse);

        when(animalRepository.findAllSpiders()).thenReturn(spiders);
        when(animalMapper.toResponseList(spiders)).thenReturn(responses);

        List<AnimalResponse> result = animalService.getSpiders();

        assertFalse(result.isEmpty());
        verify(animalRepository).findAllSpiders();
    }

    @Test
    void shouldReturnEmptyListWhenNoSpidersFound() {
        when(animalRepository.findAllSpiders()).thenReturn(List.of());
        when(animalMapper.toResponseList(any())).thenReturn(List.of());

        List<AnimalResponse> result = animalService.getSpiders();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGiveBackAnimalByName() {
        String name = "Tuptuś";
        List<Animal> animals = List.of(spider);
        List<AnimalResponse> responses = List.of(spiderResponse);

        when(animalRepository.findByNameContainingIgnoreCase(name)).thenReturn(animals);
        when(animalMapper.toResponseList(animals)).thenReturn(responses);

        List<AnimalResponse> result = animalService.findByName(name);

        assertEquals(1, result.size());
        assertEquals("Tuptuś", result.get(0).getName());
    }


    @Test
    void shouldCreateSpider() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Tuptuś");

        when(animalMapper.toSpider(request)).thenReturn(spider);
        when(animalRepository.save(spider)).thenReturn(spider);
        when(animalMapper.territoryToResponse(spider)).thenReturn(spiderResponse);

        AnimalResponse result = animalService.createSpider(request);

        assertNotNull(result);
        assertEquals("Tuptuś", result.getName());
    }

    @Test
    void shouldCreateSmallReptile() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Bubuś");

        when(animalMapper.toSmallReptile(request)).thenReturn(smallReptile);
        when(animalRepository.save(smallReptile)).thenReturn(smallReptile);
        when(animalMapper.territoryToResponse(smallReptile)).thenReturn(smallReptileResponse);

        AnimalResponse result = animalService.createSmallReptile(request);

        assertEquals("Bubuś", result.getName());
    }


    @Test
    void shouldDeleteAnimalWhenExists() {
        when(animalRepository.existsById(5L)).thenReturn(true);

        assertDoesNotThrow(() -> animalService.delete(5L));
        verify(animalRepository).deleteById(5L);
    }

    @Test
    void shouldThrowExceptionWhenDeletedAnimalNotExists() {
        when(animalRepository.existsById(99L)).thenReturn(false);

        assertThrows(AnimalWithIDNotFoundException.class, () -> animalService.delete(99L));
        verify(animalRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenAnimalNotFoundById() {
        when(animalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AnimalWithIDNotFoundException.class, () -> animalService.getById(999L));
    }

    @Test
    void shouldThrowExceptionWhenBigReptileMappingFails() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        BigReptile reptile = new BigReptile();
        AnimalResponse wrongResponse = new AnimalResponse();

        when(animalMapper.toBigReptile(request)).thenReturn(reptile);
        when(animalRepository.save(reptile)).thenReturn(reptile);
        when(animalMapper.territoryToResponse(reptile)).thenReturn(wrongResponse);

        assertThrows(WeightRequiredForBigReptileException.class, () -> animalService.createBigReptile(request));
    }


    @Test
    void spiderShouldBeReadyForFoodAfterExactly14Days() {
        spider.setLastFeedingDate(LocalDate.now().minusDays(14));
        assertTrue(spider.canBeFed(LocalDate.now()));
    }

    @Test
    void spiderShouldNotBeReadyForFoodAfter13Days() {
        spider.setLastFeedingDate(LocalDate.now().minusDays(13));
        assertFalse(spider.canBeFed(LocalDate.now()));
    }

    @Test
    void smallReptileShouldBeReadyAfter7Days() {
        smallReptile.setLastFeedingDate(LocalDate.now().minusDays(7));
        assertTrue(smallReptile.canBeFed(LocalDate.now()));
    }

    @Test
    void humidityShouldDecreaseDaily() {
        LocalDate today = LocalDate.of(2026, 3, 20);
        spider.setLastHumidityRefillDate(today.minusDays(10));
        assertEquals(90.0, spider.calculateCurrentHumidity(today));
    }

    @Test
    void humidityShouldNotBeNegative() {
        LocalDate today = LocalDate.of(2026, 3, 20);
        spider.setLastHumidityRefillDate(today.minusDays(200));
        assertEquals(0.0, spider.calculateCurrentHumidity(today));
    }

    @Test
    void humidityShouldBeZeroWhenDateIsNull() {
        spider.setLastHumidityRefillDate(null);
        assertEquals(0.0, spider.calculateCurrentHumidity(LocalDate.of(2026, 3, 20)));
    }

    @Test
    void shouldMapRequestToSpiderUsingLocalMapper() {
        AnimalMapper localMapper = new AnimalMapper();
        AnimalRequest req = new AnimalRequest();
        req.setName("Gienek");
        Spider result = localMapper.toSpider(req);
        assertEquals("Gienek", result.getName());
    }

    @Test
    void humidityShouldBeFullWhenRefillDateIsInTheFuture() {
        LocalDate today = LocalDate.of(2026, 3, 20);
        spider.setLastHumidityRefillDate(today.plusDays(2));
        assertEquals(100.0, spider.calculateCurrentHumidity(today));
    }
}
