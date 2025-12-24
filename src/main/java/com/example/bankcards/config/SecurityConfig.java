package com.example.bankcards.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.bankcards.config.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                // Настраиваем авторизацию
                .authorizeHttpRequests(auth -> auth
                    // Разрешаем доступ к Swagger без авторизации
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll() // Остальные эндпоинты требуют аутентификации

                    .requestMatchers(HttpMethod.POST, "/cards/{cardId}/request-block").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/cards/transfer").hasRole("USER")
                    .requestMatchers("/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET,"/cards/**").hasAnyRole("USER","ADMIN")
                    .requestMatchers(HttpMethod.POST,"/cards/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                );

        return http.build();
    }
}
