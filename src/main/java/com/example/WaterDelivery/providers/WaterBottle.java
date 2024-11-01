package com.example.WaterDelivery.providers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "water_bottle")
public class WaterBottle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "size_liters", nullable = false)
    private Integer sizeLiters = 20; // Фиксированный объем

    @Column(name = "price", nullable = false)
    private Double price = 500.0; // Фиксированная цена

    @Column(name = "url")
    private String url = "/images/water.png"; // URL изображения

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1; // Значение по умолчанию для количества
}
