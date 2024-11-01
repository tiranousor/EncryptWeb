package com.example.WaterDelivery.repositories;

import com.example.WaterDelivery.providers.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    void deleteAllByWaterBottleId(int id);
}
