package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class UserController {

    private final UserService userService; // сервис для работы с пользователями

    /*
     * метод создание пользователя
     * */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
    	System.out.println("Received DTO: " + dto);
    	return ResponseEntity.ok(userService.createUser(dto));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User delete");
    }

}
