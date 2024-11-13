package com.example.WaterDelivery.repositories;

import com.example.WaterDelivery.providers.Message;
import com.example.WaterDelivery.providers.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(Person sender);
    List<Message> findBySenderAndReceiverOrderByIdAsc(Person sender, Person receiver);
    List<Message> findByReceiver(Person receiver);
}
