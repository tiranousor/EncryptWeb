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
    private Integer sizeLiters;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "url")
    private String url;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
