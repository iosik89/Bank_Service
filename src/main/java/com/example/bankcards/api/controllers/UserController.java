package com.example.bankcards.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.api.dto.UserDto;
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
	 * Создаёт нового пользователя в системе.
	 * 
	 * Метод принимает DTO с данными пользователя,
	 * сохраняет его в базе данных и возвращает созданный объект.
	 *
	 * @param dto объект UserDto с данными для создания пользователя
	 * @return ResponseEntity с созданным пользователем (UserDto)
     * */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
    	return ResponseEntity.ok(userService.createUser(dto));
    }
    
    /**
     * Удаляет пользователя по его идентификатору.
     *
     * Если пользователь не найден, в сервисе выбрасывается исключение.
     *
     * @param id идентификатор пользователя для удаления
     * @return ResponseEntity с сообщением об успешном удалении
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User delete");
    }

}
