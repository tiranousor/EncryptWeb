package com.example.WaterDelivery.repositories;


import com.example.WaterDelivery.providers.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KeyRepository extends JpaRepository<Key, Long> {
    Optional<Key> findByUserIdAndMethod(Long userId, String method);
}
