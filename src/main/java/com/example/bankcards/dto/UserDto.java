package com.example.bankcards.dto;

import com.example.bankcards.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private User.Role role;
    
    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getFullName(), user.getRole());
    }

//    // Преобразование из сущности в DTO
//    public static UserDto fromEntity(User user) {
//        UserDto dto = new UserDto();
//        dto.setId(user.getId());
//        dto.setUsername(user.getUsername());
//        dto.setFullName(user.getFullName());
//        dto.setRole(user.getRole());
//        return dto;
//    }

//    // Преобразование из DTO в сущность
//    public User toEntity() {
//        User user = new User();
//        user.setId(this.id); // Обычно id создаётся в БД, но можно оставить для обновления
//        user.setUsername(this.username);
//        user.setFullName(this.fullName);
//        user.setRole(this.role);
//        // Пароль нужно задавать отдельно при создании/хешировании
//        return user;
//    }
}
