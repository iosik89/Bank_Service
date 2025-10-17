package com.example.bankcards.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.Card;


public interface CardService {
	
    /*
	 * Создание карты
	 * role(ADMIN)
	 * */
    @PreAuthorize("hasRole('ADMIN')")
	Card createCard(CardDto dto);
	
	/*
	 * Перевод между своими картами
	 * role(USER)
	 * */
	void transferBetweenOwnCards(Long userId, Long fromCardId, Long toCardId, BigDecimal amount); 


	Page<CardDto> getUserCards(Long userId, CardSearchRequest searchRequest);

	Page<CardDto> searchUserCards(Long userId, String query, PageRequest pageable);
	
	BigDecimal getCardBalance(Long cardId, Long userId);
	
	void requestBlockCard(Long userId, Long cardId);
	
	void blockCard(Long cardId);
	
	void deleteCard(Long cardId);

	Page<CardDto> getAllCards(Pageable pageable);
	
}
