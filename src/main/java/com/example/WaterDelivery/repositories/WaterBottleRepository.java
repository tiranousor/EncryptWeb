package com.example.WaterDelivery.repositories;

import com.example.WaterDelivery.providers.WaterBottle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaterBottleRepository extends JpaRepository<WaterBottle, Integer> {
    Optional<WaterBottle> findBySizeLitersAndPrice(Integer sizeLiters, Double price);

}
