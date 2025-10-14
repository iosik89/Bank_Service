package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;

	public CardServiceImpl(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	@Override
	public Card getById(Long id) {
		Optional<Card> card = cardRepository.findById(id);
		return card.orElseThrow(() -> new RuntimeException("Карта с id " + id + " не найдена"));
	}

	@Override
	@Transactional
	public Card createCard(CardDto dto) {
		Card card = new Card();
		card.setPan(dto.getPan());
		card.setPanHash(CardUtil.hashPan(dto.getPan()));
		card.setLast4(CardUtil.getLast4(dto.getPan()));
		card.setHolderName(dto.getHolderName());
		card.setExpirationDate(dto.getExpirationDate());
		card.setStatus(dto.getStatus());
		card.setBalance(dto.getBalance());
		return cardRepository.save(card);
	}
}