package com.example.WaterDelivery.providers;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "user_contacts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "contact_id", "method"})
})
@Data
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    private User contact;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(nullable = false)
    private String method;
}
