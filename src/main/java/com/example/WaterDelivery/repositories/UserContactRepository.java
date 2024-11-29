package com.example.WaterDelivery.repositories;

import com.example.WaterDelivery.providers.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {
    Optional<UserContact> findByUserAndContactAndMethod(User user, User contact, String method);
    List<UserContact> findByUser(User user);

    Optional<UserContact> findByUserIdAndContactIdAndMethod(Long userId, Long contactId, String lowerCase);
}
