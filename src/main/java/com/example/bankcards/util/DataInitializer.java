package com.example.bankcards.util;

import com.example.bankcards.store.entities.User;
import com.example.bankcards.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
 * Создание пользователя по умолчанию
 * */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setRole(User.Role.ADMIN);
            admin.setPassword(encoder.encode("admin123"));
            userRepository.save(admin);
        }
    }
}
