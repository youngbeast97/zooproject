package com.zoo.zoo.service.feeding;

import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodSupplyService {

    private final FoodInventoryRepository inventoryRepository;

    @Scheduled(cron = "0 */5 * * * *") //tu sie cos pojebalo bo sie okazalo ze do SCHEDULED komp i caly system musi ciagle dzialac a to do dupy
    @Transactional
    public void restockFood() {
        List<FoodInventory> inventory = inventoryRepository.findAll();
        for (FoodInventory item : inventory) {
            item.setCurrentQuantity(item.getMaxQuantity());
        }

        inventoryRepository.saveAll(inventory);
    }
//    TODO: sprobowac zrobic ten gowniany scheduled

@Transactional
public void restockIfNeeded() {
    List<FoodInventory> inventory = inventoryRepository.findAll();
    LocalDate today = LocalDate.now();

    for (FoodInventory item : inventory) {
        LocalDate lastRestock = item.getLastRestockDate();
        // jeśli brak daty lub minęły 2 tygodnie
        if (lastRestock == null || lastRestock.plusWeeks(2).isBefore(today) || lastRestock.plusWeeks(2).isEqual(today)) {
            item.setCurrentQuantity(item.getMaxQuantity());
            item.setLastRestockDate(today);
        }
    }

    inventoryRepository.saveAll(inventory);
}
}


