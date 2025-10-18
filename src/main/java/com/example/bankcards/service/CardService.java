package com.example.bankcards.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;


public interface CardService {
	

	Card createCard(CardDto dto);

	void transferBetweenOwnCards(Long userId, Long fromCardId, Long toCardId, BigDecimal amount); 


	Page<CardDto> getUserCards(Long userId, int page, int size);
	
    Page<CardDto> searchUserCards(Long userId, String query, String panLast4, Card.CardStatus status, int page, int size);
    
    Page<CardDto> searchUserCards(Long userId, String query, PageRequest pageable);
	
	BigDecimal getCardBalance(Long cardId, Long userId);
	
	void requestBlockCard(Long userId, Long cardId);
	
	void blockCard(Long cardId);
	
	void deleteCard(Long cardId);

	Page<CardDto> getAllCards(Pageable pageable);
	
}
