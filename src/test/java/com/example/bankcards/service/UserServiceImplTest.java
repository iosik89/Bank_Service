package com.example.bankcards.service;

import com.example.bankcards.api.dto.UserDto;
import com.example.bankcards.store.entities.User;
import com.example.bankcards.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        // Подготовка DTO для создания
        UserDto dto = new UserDto();
        dto.setUsername("testuser");
        dto.setPassword("password");
        dto.setRole(User.Role.ADMIN); // enum

        // Мокаем PasswordEncoder
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Мокаем UserRepository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Вызов тестируемого метода
        UserDto result = userService.createUser(dto);

        // Проверяем, что пароль зашифрован
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());

        // Проверяем имя пользователя и роль
        assertEquals("testuser", result.getUsername());
        assertEquals(User.Role.ADMIN, result.getRole()); // Сравниваем enum с enum

        // Проверка, что репозиторий вызван один раз
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
