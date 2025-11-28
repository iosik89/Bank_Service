package com.example.bankcards.controller;

import com.example.bankcards.api.controllers.UserController;
import com.example.bankcards.api.dto.UserDto;
import com.example.bankcards.store.entities.User.Role;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_shouldReturnUserDto() {
        UserDto inputDto = new UserDto();
        inputDto.setUsername("testuser");
        inputDto.setRole(Role.USER);

        UserDto returnedDto = new UserDto();
        returnedDto.setUsername("testuser");
        returnedDto.setRole(Role.USER);

        when(userService.createUser(inputDto)).thenReturn(returnedDto);

        ResponseEntity<UserDto> response = userController.createUser(inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(Role.USER, response.getBody().getRole());

        verify(userService, times(1)).createUser(inputDto);
    }

    @Test
    void deleteUser_shouldReturnOkMessage() {
        Long userId = 1L;

        ResponseEntity<String> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User delete", response.getBody());

        verify(userService, times(1)).deleteUser(userId);
    }
}
