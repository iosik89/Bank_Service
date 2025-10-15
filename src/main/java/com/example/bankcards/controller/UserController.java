package com.example.bankcards.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService; // сервис для работы с пользователями

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- Методы для ADMIN ---
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        User user = userService.createUser(dto);
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --- Методы для USER ---
    @GetMapping("/{id}/balance")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable Long id) {
        BigDecimal balance = userService.getUserBalance(id);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/cards")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CardDto>> getUserCards(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String last4,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CardSearchRequest searchRequest = new CardSearchRequest(last4, status, page, size);
        Page<CardDto> cards = userService.getUserCards(id, searchRequest);
        return ResponseEntity.ok(cards);
    }
}
