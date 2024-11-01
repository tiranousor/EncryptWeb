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

    @Column(name = "role", nullable = false)
    private String role = "ROLE_USER";

    // Дополнительные поля для доставки
    @NotEmpty(message = "Адрес не должен быть пустым")
    @Column(name = "address", nullable = false)
    private String address;

    @NotEmpty(message = "Номер телефона не должен быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Некорректный формат номера телефона")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Если требуется, можно добавить email
    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
}
