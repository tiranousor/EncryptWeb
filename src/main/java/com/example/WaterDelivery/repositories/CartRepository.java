package com.example.WaterDelivery.repositories;

import com.example.WaterDelivery.providers.Cart;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.providers.WaterBottle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Заменено на WaterBottleId
    public Cart findByPersonIdAndWaterBottleId(Integer person_id, Integer waterBottle_id);

    public List<Cart> findAllByPersonRole(String personRole);

    public Integer countByPersonId(Integer person_id);

    public List<Cart> findByPerson(Person person);

    // Заменено на WaterBottleId
    @Transactional
    void deleteAllByWaterBottleId(int id);
}
