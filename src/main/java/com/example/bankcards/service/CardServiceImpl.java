package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.DuplicateCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;

	public CardServiceImpl(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	@Override
	public Card getById(Long cardId) {
		Optional<Card> card = cardRepository.findById(cardId);
		return card.orElseThrow(() -> new RuntimeException("Карта с id " + cardId + " не найдена"));
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public Card createCard(CardDto dto) {
		
	    String panHash = CardUtil.hashPan(dto.getPan());
	    String last4 = CardUtil.getLast4(dto.getPan());	
		
	    if (cardRepository.existsByPanHash(panHash)) {
	        throw new DuplicateCardException("Карта с таким PAN уже существует");
	    }
		
		Card card = new Card();
		card.setPan(dto.getPan());
		card.setPanHash(panHash);
		card.setLast4(last4);
		card.setHolderName(dto.getHolderName());
		card.setExpirationDate(dto.getExpirationDate());
		card.setStatus(dto.getStatus());
		card.setBalance(dto.getBalance());
		return cardRepository.save(card);
	}
	
	@Override
	@Transactional(readOnly = true)
    public Page<CardDto> getUserCards(Long userId, CardSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize()); //пагинация (страница и размер)

        Page<Card> cards;
        if (searchRequest.getStatus() != null) {
            cards = cardRepository.findByUserIdAndStatus(
                    userId,
                    Card.CardStatus.valueOf(searchRequest.getStatus().toUpperCase()),
                    pageable
            );
        } else if (searchRequest.getPanLast4() != null) {
            cards = cardRepository.findByUserIdAndLast4Containing(
                    userId, searchRequest.getPanLast4(), pageable
            );
        } else {
            cards = cardRepository.findByUserId(userId, pageable);
        }

        return cards.map(CardDto::fromEntity);
    }

	@Override
	public BigDecimal getBalance(Long cardId) {
	    Card card = cardRepository.findById(cardId)
	            .orElseThrow(() -> new RuntimeException("Карта с id " + cardId + " не найдена"));
	    return card.getBalance();
	}
	
}