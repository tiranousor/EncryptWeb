package com.example.WaterDelivery.providers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "keys", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "method"})
})
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String keyData;
}