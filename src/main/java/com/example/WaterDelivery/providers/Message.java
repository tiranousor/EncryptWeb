package com.example.WaterDelivery.providers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // Зашифрованное сообщение

    @Column(name = "encryption_method")
    private String encryptionMethod;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Person sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Person receiver;
}
