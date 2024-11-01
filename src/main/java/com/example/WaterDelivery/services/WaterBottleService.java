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
    public void initializeWaterBottles() {
        if (waterBottleRepository.count() == 0) {
            // Создаем различные типы бутылок с разным объемом и ценой
            WaterBottle bottle5L = new WaterBottle(null, 5, 150.0, "/images/water.png", 1);
            WaterBottle bottle10L = new WaterBottle(null, 10, 250.0, "/images/water.png", 1);
            WaterBottle bottle19L = new WaterBottle(null, 19, 400.0, "/images/water.png", 1);
            WaterBottle bottle30L = new WaterBottle(null, 30, 750.0, "/images/water.png", 1);

            waterBottleRepository.save(bottle5L);
            waterBottleRepository.save(bottle10L);
            waterBottleRepository.save(bottle19L);
            waterBottleRepository.save(bottle30L);
        }
    }
}
