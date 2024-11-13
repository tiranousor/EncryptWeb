package com.example.WaterDelivery.config;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.security.PersonDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Получаем объект PersonDetails
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Проверяем роли и перенаправляем
        boolean isUser = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (isUser) {
            response.sendRedirect("/messages"); // Перенаправляем на страницу сообщений
        } else {
            // В случае отсутствия роли перенаправляем на страницу ошибки
            response.sendRedirect("/login?error=true");
        }
    }
}
