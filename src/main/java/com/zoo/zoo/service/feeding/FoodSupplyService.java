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

    static final double LOW_STOCK_RATIO = 0.2;

    private final FoodInventoryRepository inventoryRepository;

    @Scheduled(cron = "0 */5 * * * *") //tu sie cos pojebalo bo sie okazalo ze do SCHEDULED komp i caly system musi ciagle dzialac a to do dupy
    @Transactional
    public void restockFood() {
        List<FoodInventory> inventory = inventoryRepository.findAll();
        for (FoodInventory item : inventory) {
            if (item.getMaxQuantity() == null) {
                log.warn("Skipping {}: max quantity not configured", item.getFoodType());
                continue;
            }
            item.setCurrentQuantity(item.getMaxQuantity());
        }

        inventoryRepository.saveAll(inventory);
    }
//    TODO: sprobowac zrobic ten gowniany scheduled

// HINT: FEATURE - besides the 2-week schedule, an item is restocked early when it drops below
// HINT: LOW_STOCK_RATIO of its capacity, and the caller learns how many items were restocked.
// HINT: If the surrounding code was restructured, the rule itself (not this exact code shape) is what must survive.
@Transactional
public int restockIfNeeded() {
    List<FoodInventory> inventory = inventoryRepository.findAll();
    LocalDate today = LocalDate.now();
    int restocked = 0;

    for (FoodInventory item : inventory) {
        // HINT: Rows imported from the old warehouse system have no max quantity - they must be skipped
        // HINT: by BOTH restock paths (scheduled restockFood and restockIfNeeded).
        if (item.getMaxQuantity() == null) {
            log.warn("Skipping {}: max quantity not configured", item.getFoodType());
            continue;
        }
        LocalDate lastRestock = item.getLastRestockDate();
        boolean scheduled = lastRestock == null || !lastRestock.plusWeeks(2).isAfter(today);
        boolean runningLow = item.getCurrentQuantity() < item.getMaxQuantity() * LOW_STOCK_RATIO;
        if (scheduled || runningLow) {
            item.setCurrentQuantity(item.getMaxQuantity());
            item.setLastRestockDate(today);
            restocked++;
        }
    }

    inventoryRepository.saveAll(inventory);
    log.info("Restocked {} of {} food items", restocked, inventory.size());
    return restocked;
}
}


