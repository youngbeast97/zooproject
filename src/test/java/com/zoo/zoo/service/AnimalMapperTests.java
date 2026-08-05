package com.zoo.zoo.service;

import com.zoo.zoo.model.animal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalMapperTests {

    private AnimalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AnimalMapper();
    }


    @Test
    void shouldMapRequestToSpiderEntity() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Zenon");
        request.setSpecies("Ptasznik");

        Spider result = mapper.toSpider(request);

        assertNotNull(result);
        assertEquals("Zenon", result.getName());
        assertEquals("Ptasznik", result.getSpecies());
    }

    @Test
    void shouldMapRequestToSmallReptileEntity() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Leon");
        request.setSpecies("Gekon");

        SmallReptile result = mapper.toSmallReptile(request);

        assertNotNull(result);
        assertEquals("Leon", result.getName());
    }

    @Test
    void shouldMapWeightRequestToBigReptileEntity() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        request.setName("Smok");
        request.setWeightInGrams(15000);

        BigReptile result = mapper.toBigReptile(request);

        assertNotNull(result);
        assertEquals(15000, result.getWeightInGrams());
    }

    @Test
    void shouldMapWeightRequestToVenomousReptileEntity() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        request.setWeightInGrams(2500);

        VenomousReptile result = mapper.toVenomousReptile(request);

        assertEquals(2500, result.getWeightInGrams());
    }
    @Test
    void shouldMapSpiderToGenericResponse() {
        Spider spider = new Spider();
        spider.setId(10L);
        spider.setName("Tekla");

        AnimalResponse response = mapper.territoryToResponse(spider);

        assertEquals(10L, response.getId());
        assertEquals("Tekla", response.getName());
        assertFalse(response instanceof AnimalWithWeightResponse);
    }

    @Test
    void shouldMapBigReptileToWeightResponse() {
        BigReptile reptile = new BigReptile();
        reptile.setId(5L);
        reptile.setWeightInGrams(8000);

        AnimalResponse response = mapper.territoryToResponse(reptile);

        assertTrue(response instanceof AnimalWithWeightResponse);
        assertEquals(8000, ((AnimalWithWeightResponse) response).getWeightInGrams());
    }

    @Test
    void shouldMapVenomousReptileToWeightResponse() {
        VenomousReptile reptile = new VenomousReptile();
        reptile.setWeightInGrams(1200);

        AnimalResponse response = mapper.territoryToResponse(reptile);

        assertTrue(response instanceof AnimalWithWeightResponse);
        assertEquals(1200, ((AnimalWithWeightResponse) response).getWeightInGrams());
    }


    @Test
    void shouldReturnEmptyListWhenInputListIsNull() {
        List<AnimalResponse> result = mapper.toResponseList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenInputListIsEmpty() {
        List<AnimalResponse> result = mapper.toResponseList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMapListWithVariousAnimalsCorrectly() {
        Spider s = new Spider(); s.setName("Spider");
        BigReptile b = new BigReptile(); b.setName("Big");
        List<Animal> animals = List.of(s, b);

        List<AnimalResponse> result = mapper.toResponseList(animals);

        assertEquals(2, result.size());
        assertEquals("Spider", result.get(0).getName());
        assertTrue(result.get(1) instanceof AnimalWithWeightResponse);
    }


    @Test
    void shouldHandleNullNameInRequestMapping() {
        AnimalRequest request = new AnimalRequest();
        request.setName(null);

        Spider result = mapper.toSpider(request);

        assertNotNull(result);
        assertNull(result.getName());
    }

    @Test
    void shouldHandleZeroWeightInBigReptileMapping() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        request.setWeightInGrams(0);

        BigReptile result = mapper.toBigReptile(request);

        assertEquals(0, result.getWeightInGrams());
    }

    @Test
    void shouldHandleLargeWeightInVenomousReptileMapping() {
        AnimalWithWeightRequest request = new AnimalWithWeightRequest();
        request.setWeightInGrams(Integer.MAX_VALUE);

        VenomousReptile result = mapper.toVenomousReptile(request);

        assertEquals(Integer.MAX_VALUE, result.getWeightInGrams());
    }

    @Test
    void shouldMapEmptySpiderToResponseWithNulls() {
        Spider emptySpider = new Spider();
        AnimalResponse response = mapper.territoryToResponse(emptySpider);

        assertNotNull(response);
        assertNull(response.getName());
        assertNull(response.getId());
    }

    @Test
    void shouldNotFailWhenMappingListWithOneNullElement() {
        List<Animal> animals = new ArrayList<>();
        animals.add(null);

        assertDoesNotThrow(() -> mapper.toResponseList(animals));
    }
}