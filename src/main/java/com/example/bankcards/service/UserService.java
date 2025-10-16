package com.example.bankcards.service;

import com.example.bankcards.entity.User;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.dto.UserDto;

public interface UserService {
	
    UserDto createUser(UserDto dto);
//    void deleteUser(Long id);
//    BigDecimal getUserBalance(Long userId);
//	Page<CardDto> getUserCards(Long id, CardSearchRequest searchRequest);
}

