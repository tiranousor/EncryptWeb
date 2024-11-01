package com.example.WaterDelivery.services;

import com.example.WaterDelivery.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private final CartRepository orderItemRepository;

    @Autowired
    public OrderItemService(CartRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }
    @Transactional
    public void deleteAll(int waterBottleId){
        orderItemRepository.deleteAllByWaterBottleId(waterBottleId);
    }
}
