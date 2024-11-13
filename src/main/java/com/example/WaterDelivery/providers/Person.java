package com.example.WaterDelivery.providers;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name="avatarUrl")
    private String avatarUrl;

    @Column(name="about")
    private String about;

    @Column(name = "role", nullable = false)
    private String role = "ROLE_USER";

    // Если требуется, можно добавить email
    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "rsa_private_key", columnDefinition = "TEXT")
    private String rsaPrivateKey;

    @Column(name = "rsa_public_key", columnDefinition = "TEXT")
    private String rsaPublicKey;

    @Column(name = "aes_key", columnDefinition = "TEXT")
    private String aesKey;

}
