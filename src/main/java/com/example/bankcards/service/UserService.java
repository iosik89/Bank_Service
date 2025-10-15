package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.dto.UserDto;

public interface UserService {
	
    User createUser(UserDto dto);
    void deleteUser(Long id);
  
}

