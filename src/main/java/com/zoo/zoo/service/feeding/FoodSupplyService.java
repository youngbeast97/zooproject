package com.zoo.zoo.service.feeding;

import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodSupplyService {

    static final Period RESTOCK_INTERVAL = Period.ofWeeks(2);

    private final FoodInventoryRepository inventoryRepository;

    // Full restock of every item (demo schedule: every 5 minutes), ignoring RESTOCK_INTERVAL.
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void restockFood() {
        LocalDate today = LocalDate.now();
        List<FoodInventory> inventory = inventoryRepository.findAll();
        inventory.forEach(item -> restock(item, today));
        inventoryRepository.saveAll(inventory);
    }

    // HINT: REFACTOR - "should this item be restocked?" is decided only in isRestockDue(),
    // HINT: "how is it restocked?" only in restock(). Both public methods share these helpers,
    // HINT: so a rule added in one helper automatically applies to every restock path that uses it.
    @Transactional
    public void restockIfNeeded() {
        LocalDate today = LocalDate.now();
        List<FoodInventory> inventory = inventoryRepository.findAll();
        inventory.stream()
                .filter(item -> isRestockDue(item, today))
                .forEach(item -> restock(item, today));
        inventoryRepository.saveAll(inventory);
    }

    boolean isRestockDue(FoodInventory item, LocalDate today) {
        LocalDate lastRestock = item.getLastRestockDate();
        return lastRestock == null || !lastRestock.plus(RESTOCK_INTERVAL).isAfter(today);
    }

    private void restock(FoodInventory item, LocalDate today) {
        item.setCurrentQuantity(item.getMaxQuantity());
        item.setLastRestockDate(today);
    }
}
