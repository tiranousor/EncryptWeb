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
        String referer = request.getParameter("referer");
        // Получаем объект ClientDetails
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Получаем список ролей
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/orders");
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect("/catalog");
        } else {
            // В случае отсутствия подходящей роли можно направить на страницу ошибки
            response.sendRedirect("/login?error");
        }
    }
}
