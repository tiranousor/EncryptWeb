package com.example.WaterDelivery.config;

import com.example.WaterDelivery.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final String[] AUTH_WHITELIST = {
            "/", "/login", "/images/**", "/*"
    };
    private final UserDetailsService personDetailsService;
    @Autowired
    public SecurityConfiguration(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(personDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/add-water-bottle", "/edit-water-bottle/**", "/delete-water-bottle/**").hasRole("ADMIN")
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/login") // страница для логина``
                        .loginProcessingUrl("/process_login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll() // разрешаем доступ ко всем
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}