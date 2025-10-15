package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Отключаем CSRF для API и WebSocket
            .csrf(AbstractHttpConfigurer::disable)
            
            // Настраиваем CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Настраиваем авторизацию
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ws/**").permitAll()  // WebSocket endpoint
                .requestMatchers("/api/chat/**").permitAll()  // Chat API
                .requestMatchers("/api/v2/**").permitAll()  // New Entity API
                .requestMatchers("/api/users/**").permitAll()  // Users API  
                .requestMatchers("/h2-console/**").permitAll()  // H2 console для разработки
                .requestMatchers("/actuator/**").permitAll()  // Actuator endpoints
                .anyRequest().authenticated()
            )
            
            // Настраиваем заголовки безопасности
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())  // Для H2 console
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}