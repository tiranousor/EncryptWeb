package com.example.WaterDelivery.providers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Отправитель
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Получатель
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedMessage;

    @Column(nullable = false)
    private String method; // "caesar", "aes", "rsa"

    @Column(nullable = false)
    private LocalDateTime timestamp;}
