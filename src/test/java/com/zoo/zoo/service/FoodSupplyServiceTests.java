package com.zoo.zoo.service;

import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.repository.food.FoodInventoryRepository;
import com.zoo.zoo.service.feeding.FoodSupplyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodSupplyServiceTests {

    @Mock
    private FoodInventoryRepository inventoryRepository;

    @InjectMocks
    private FoodSupplyService foodSupplyService;

    @Test
    void shouldRestockItemsWhenNeeded() {
        LocalDate twoWeeksAgo = LocalDate.now()
                .minusWeeks(2);
        FoodInventory item1 = new FoodInventory();
        item1.setCurrentQuantity(2);
        item1.setMaxQuantity(10);
        item1.setLastRestockDate(twoWeeksAgo);

        FoodInventory item2 = new FoodInventory();
        item2.setCurrentQuantity(5);
        item2.setMaxQuantity(15);
        item2.setLastRestockDate(null);

        List<FoodInventory> inventory = Arrays.asList(item1, item2);
        when(inventoryRepository.findAll()).thenReturn(inventory);

        foodSupplyService.restockIfNeeded();
        for (FoodInventory item : inventory) {
            assert (item.getCurrentQuantity() == item.getMaxQuantity());
            assert (item.getLastRestockDate()
                    .equals(LocalDate.now()));
        }

        verify(inventoryRepository).saveAll(inventory);
    }
    @Test
    void shouldNotRestockWhenFoodIsFresh() {
        LocalDate weekAgo = LocalDate.now().minusWeeks(1);
        FoodInventory item = new FoodInventory();
        item.setCurrentQuantity(5);
        item.setMaxQuantity(100);
        item.setLastRestockDate(weekAgo);

        when(inventoryRepository.findAll()).thenReturn(List.of(item));

        foodSupplyService.restockIfNeeded();

        assertEquals(5, item.getCurrentQuantity());
        verify(inventoryRepository).saveAll(anyList());
    }

    @Test
    void shouldRestockAllRegardlessOfDateInRestockFood() {
        FoodInventory item = new FoodInventory();
        item.setCurrentQuantity(0);
        item.setMaxQuantity(50);
        when(inventoryRepository.findAll()).thenReturn(List.of(item));

        foodSupplyService.restockFood();

        assertEquals(50, item.getCurrentQuantity());
        verify(inventoryRepository).saveAll(anyList());
    }
}