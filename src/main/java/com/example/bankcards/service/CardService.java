package com.example.bankcards.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.Card;


public interface CardService {
    Card getById(Long id);
//	Page<CardDto> getUserCards(Long userId, CardSearchRequest searchRequest);
	Card createCard(CardDto dto);
	BigDecimal getBalance(Long id);
	List<Card> getUserCards(Long userId);
	
}
