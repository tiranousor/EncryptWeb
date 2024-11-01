package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.repositories.WaterBottleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class WaterBottleService {

    private final WaterBottleRepository waterBottleRepository;

    @Autowired
    public WaterBottleService(WaterBottleRepository waterBottleRepository) {
        this.waterBottleRepository = waterBottleRepository;
    }

    public List<WaterBottle> findAll() {
        return waterBottleRepository.findAll();
    }

    public WaterBottle findOne(int id) {
        return waterBottleRepository.findById(id).orElse(null);
    }

    @PostConstruct
    public void initializeWaterBottle() {
        if (waterBottleRepository.count() == 0) {
            WaterBottle defaultBottle = new WaterBottle();
            defaultBottle.setSizeLiters(20);
            defaultBottle.setPrice(500.0);
            defaultBottle.setUrl("/images/water.png");
            defaultBottle.setQuantity(1);
            waterBottleRepository.save(defaultBottle);
        }
    }
}
