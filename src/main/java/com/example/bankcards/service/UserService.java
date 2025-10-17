package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;

public interface UserService {
	
    UserDto createUser(UserDto dto);
    
	void deleteUser(Long id);
}

