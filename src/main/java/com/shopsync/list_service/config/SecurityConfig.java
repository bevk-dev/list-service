package com.shopsync.list_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Zaščitimo vse API poti za sezname, artikle in produkte
                        .requestMatchers("/api/shopping-lists/**").authenticated()
                        .requestMatchers("/api/items/**").authenticated()
                        .requestMatchers("/api/products/**").authenticated()
                        // Vse ostalo (npr. actuator ali javni opisi) naj bo odprto
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()));

        return http.build();
    }
}