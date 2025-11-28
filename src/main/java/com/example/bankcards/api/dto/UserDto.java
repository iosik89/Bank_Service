package com.example.bankcards.api.dto;

import com.example.bankcards.store.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private User.Role role;
    private String password;

    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            null // пароль не возвращаем
        );
    }
}
