package com.zoo.zoo.model.animal;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnimalMapper {

    public Spider toSpider(AnimalRequest request) {
        if (request == null) return null;
        Spider spider = new Spider();
        mapBaseToEntity(spider, request);
        return spider;
    }

    public SmallReptile toSmallReptile(AnimalRequest request) {
        if (request == null) return null;
        SmallReptile reptile = new SmallReptile();
        mapBaseToEntity(reptile, request);
        return reptile;
    }

    public BigReptile toBigReptile(AnimalWithWeightRequest request) {
        if (request == null) return null;
        BigReptile reptile = new BigReptile();
        mapBaseToEntity(reptile, request);
        reptile.setWeightInGrams(request.getWeightInGrams());
        return reptile;
    }

    public VenomousReptile toVenomousReptile(AnimalWithWeightRequest request) {
        if (request == null) return null;
        VenomousReptile reptile = new VenomousReptile();
        mapBaseToEntity(reptile, request);
        reptile.setWeightInGrams(request.getWeightInGrams());
        return reptile;
    }

    public AnimalResponse territoryToResponse(Animal animal) {
        if (animal == null) return null;

        if (animal instanceof BigReptile br) {
            return mapWithWeightToResponse(br, "BIG_REPTILE", br.getWeightInGrams());
        }
        if (animal instanceof VenomousReptile vr) {
            return mapWithWeightToResponse(vr, "VENOMOUS_REPTILE", vr.getWeightInGrams());
        }
        if (animal instanceof Spider s) {
            return mapBaseToResponse(s, "SPIDER");
        }
        if (animal instanceof SmallReptile sr) {
            return mapBaseToResponse(sr, "SMALL_REPTILE");
        }

        return null;
    }
    private AnimalWithWeightResponse mapWithWeightToResponse(Animal animal, String type, Integer weight) {
        AnimalWithWeightResponse response = new AnimalWithWeightResponse();
        fillCommonFields(response, animal, type);
        response.setWeightInGrams(weight);
        return response;
    }


    public List<AnimalResponse> toResponseList(List<Animal> animals) {
        if (animals == null || animals.isEmpty()) {
            return new ArrayList<>(); // Zwracamy pustą listę zamiast null
        }
        return animals.stream()
                .map(this::territoryToResponse)
                .toList();
    }

    private void mapBaseToEntity(Animal entity, AnimalRequest request) {
        entity.setName(request.getName());
        entity.setSpecies(request.getSpecies());
        entity.setRequiresLight(request.isRequiresLight());
        entity.setLastFeedingDate(request.getLastFeedingDate());
    }

    private AnimalResponse mapBaseToResponse(Animal animal, String type) {
        AnimalResponse response = new AnimalResponse();
        fillCommonFields(response, animal, type);
        return response;
    }
    private void fillCommonFields(AnimalResponse response, Animal animal, String type) {
        Double freshHumidity = animal.calculateCurrentHumidity();

        animal.setHumidity(freshHumidity);

        response.setId(animal.getId());
        response.setName(animal.getName());
        response.setSpecies(animal.getSpecies());
        response.setHumidity(freshHumidity);
        response.setLastFeedingDate(animal.getLastFeedingDate());
        response.setType(type);
        response.setDaysSinceLastFeeding(animal.getLastFeedingDate() == null
                ? null
                : (int) ChronoUnit.DAYS.between(animal.getLastFeedingDate(), LocalDate.now()));

        if (animal.isRequiresLight()) {
            int hour = LocalTime.now().getHour();
            response.setLightStatus((hour >= 8 && hour < 20) ? "ON" : "OFF");
        }
    }
}