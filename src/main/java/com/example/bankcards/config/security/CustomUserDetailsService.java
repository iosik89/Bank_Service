package com.example.bankcards.config.security;

import com.example.bankcards.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Метод вызывается Spring Security при попытке логина
     * Здесь мы ищем пользователя в БД по username и возвращаем объект,
     * который Security сможет использовать для проверки пароля и ролей
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

