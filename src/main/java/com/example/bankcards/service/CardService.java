package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;

public interface CardService {
    Card getById(Long id);
	Card createCard(CardDto dto);
}
