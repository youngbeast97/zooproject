package com.zoo.zoo.repository.food;

import com.zoo.zoo.model.feeding.FoodInventory;
import com.zoo.zoo.model.feeding.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FoodInventoryRepository extends JpaRepository<FoodInventory,Long> {
    Optional<FoodInventory> findByFoodType(FoodType foodType);
}
