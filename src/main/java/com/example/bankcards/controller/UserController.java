package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService; // сервис для работы с пользователями

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- Методы для ADMIN ---
    /*
     * метод создание пользователя
     * */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
    	System.out.println("Received DTO: " + dto);
    	return ResponseEntity.ok(userService.createUser(dto));
    }

//    @DeleteMapping("/{id}")
//    @Transactional
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }

//    // --- Методы для USER ---
//    @GetMapping("/{id}/balance")
//    @Transactional(readOnly = true)
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable Long id) {
//        BigDecimal balance = userService.getUserBalance(id);
//        return ResponseEntity.ok(balance);
//    }

//    @GetMapping("/{id}/cards")
//    @Transactional(readOnly = true)
//	@PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<Page<CardDto>> getUserCards(
//            @PathVariable Long id,
//            @RequestParam(required = false) CardStatus status,
//            @RequestParam(required = false) String last4,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        CardSearchRequest searchRequest = new CardSearchRequest(last4, status, page, size); 
//        Page<CardDto> cards = userService.getUserCards(id, searchRequest);
//        return ResponseEntity.ok(cards);
//    }
}
