package com.example.bankcards.service;

import com.example.bankcards.api.dto.CardDto;
import com.example.bankcards.store.entities.Card;
import com.example.bankcards.store.entities.User;
import com.example.bankcards.store.entities.User.Role;
import com.example.bankcards.exception.AdminCardCreationException;
import com.example.bankcards.exception.DuplicateCardException;
import com.example.bankcards.store.repository.CardRepository;
import com.example.bankcards.store.repository.UserRepository;
import com.example.bankcards.util.CardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	
	/*
	 * Создание карты администратором
	 * */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public Card createCard(CardDto dto) {
		
		User user = null;
	    // Поиск пользователя — по ID или по имени
	    if (dto.getUserId() != null && dto.getUserId() > 0) {
	        user = userRepository.findById(dto.getUserId())
	                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + dto.getUserId() + " не найден"));
	        if (dto.getHolderName() == null || dto.getHolderName().isBlank()) {
	            dto.setHolderName(user.getUsername());
	        }
	    } else if (dto.getHolderName() != null && !dto.getHolderName().isBlank()) {
	        user = userRepository.findByUsername(dto.getHolderName())
	                .orElseThrow(() -> new RuntimeException("Пользователь с именем '" + dto.getHolderName() + "' не найден"));
	    } else {
	        throw new RuntimeException("Необходимо указать либо userId, либо holderName");
	    }
	    // Проверяем, что найденный пользователь не является администратором
	    if (user.getRole() == Role.ADMIN) {
	        throw new AdminCardCreationException("Создание карт для администратора запрещено");
	    }
	    // Проверка, что PAN не дублируется
	    String panHash = CardUtil.hashPan(dto.getPan());
	    if (cardRepository.existsByPanHash(panHash)) {
	        throw new DuplicateCardException("Карта с таким PAN уже существует");
	    }

	    // Создание карты
		Card card = new Card();
		card.setPan(dto.getPan());
		card.setPanHash(panHash);
		card.setLast4(CardUtil.getLast4(dto.getPan()));
		card.setHolderName(dto.getHolderName());
		card.setExpirationDate(dto.getExpirationDate());
		card.setStatus(dto.getStatus());
		card.setBalance(dto.getBalance());
		card.setUser(user);
		return cardRepository.save(card);
	}
	
	/*
	 * Поиск всех карты с пагинацией
	 * 
	 * */

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> getUserCards(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByUserId(userId, pageable).map(CardDto::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> searchUserCards(Long userId,
                                         String query,
                                         String panLast4,
                                         Card.CardStatus status,
                                         int page,
                                         int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 1) If status provided -> filter by status (and optionally panLast4/query)
        if (status != null) {
            if (panLast4 != null && !panLast4.isBlank()) {
                Page<Card> cards = cardRepository.findByUserIdAndStatus(userId, status, pageable);
                return cards.map(CardDto::fromEntity);
            }
            return cardRepository.findByUserIdAndStatus(userId, status, pageable).map(CardDto::fromEntity);
        }

        // 2) If exact pan last4 provided -> search by last4
        if (panLast4 != null && !panLast4.isBlank()) {
            return cardRepository.findByUserIdAndLast4Containing(userId, panLast4, pageable)
                                 .map(CardDto::fromEntity);
        }

        // 3) If holderName (query) provided -> search holderName first (case-insensitive)
        if (query != null && !query.isBlank()) {
            Page<Card> byHolder = cardRepository.findByUserIdAndHolderNameContainingIgnoreCase(userId, query, pageable);
            if (!byHolder.isEmpty()) return byHolder.map(CardDto::fromEntity);

            // if no holder hits -> search by last4 as fallback
            Page<Card> byLast4 = cardRepository.findByUserIdAndLast4Containing(userId, query, pageable);
            return byLast4.map(CardDto::fromEntity);
        }

        // 4) default: return all user's cards
        return cardRepository.findByUserId(userId, pageable).map(CardDto::fromEntity);
    }
	
	
	
	/*
	 * Запрос баланса пользователя
	 * */
	@Override
	@PreAuthorize("hasRole('USER')")
	@Transactional(readOnly = true)
	public BigDecimal getCardBalance(Long cardId, Long userId) {
	    Card card = cardRepository.findById(cardId)
	            .orElseThrow(() -> new RuntimeException("Карта не найдена"));

	    // Проверяем, что карта принадлежит пользователю
	    if (!card.getUser().getId().equals(userId)) {
	        throw new RuntimeException("Нет доступа к этой карте");
	    }

	    return card.getBalance();
	}
	
	/*
	 * Перевод между картами пользователя
	 * */
	@Override
	@PreAuthorize("hasRole('USER')")
	@Transactional
	public void transferBetweenOwnCards(Long userId, Long fromCardId, Long toCardId, BigDecimal amount) {
	    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Сумма перевода должна быть положительной");
	    }

	    // Проверяем, что обе карты принадлежат пользователю
	    Card fromCard = cardRepository.findById(fromCardId)
	            .orElseThrow(() -> new RuntimeException("Карта-отправитель не найдена"));
	    Card toCard = cardRepository.findById(toCardId)
	            .orElseThrow(() -> new RuntimeException("Карта-получатель не найдена"));

	    if (!fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) {
	        throw new SecurityException("Можно переводить только между своими картами");
	    }

	    if (fromCard.getBalance().compareTo(amount) < 0) {
	        throw new IllegalArgumentException("Недостаточно средств на карте-отправителе");
	    }

	    // Выполняем перевод
	    fromCard.setBalance(fromCard.getBalance().subtract(amount));
	    toCard.setBalance(toCard.getBalance().add(amount));

	    cardRepository.save(fromCard);
	    cardRepository.save(toCard);
	}
	
	/*
	 * Поиск держателя карты по 4last
	 * */	
	@Override
    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
	public Page<CardDto> searchUserCards(Long userId, String query, PageRequest pageable) {
        // Ищем по имени держателя или по последним 4 цифрам
        Page<Card> byHolder = cardRepository.findByUserIdAndHolderNameContainingIgnoreCase(userId, query, pageable);
        if (!byHolder.isEmpty()) return byHolder.map(CardDto::fromEntity);

        Page<Card> byLast4 = cardRepository.findByUserIdAndLast4Containing(userId, query, pageable);
        return byLast4.map(CardDto::fromEntity);
	}
	
	@Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void requestBlockCard( Long userId,Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You cannot block someone else's card");
        }

        card.setBlockRequest(true);
        cardRepository.save(card);
    }

	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public void blockCard(Long cardId) {
	    Card card = cardRepository.findById(cardId)
	            .orElseThrow(() -> new RuntimeException("Card not found"));

	    if (!card.isBlockRequest()) {
	        throw new RuntimeException("No block request for this card");
	    }

	    card.setStatus(Card.CardStatus.BLOCKED);
	    card.setBlockRequest(false); // сбросить флаг запроса
	    cardRepository.save(card);
	}

	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public void deleteCard(Long cardId) {
	    Card card = cardRepository.findById(cardId)
	            .orElseThrow(() -> new RuntimeException("Card not found"));

//	    if (!card.isBlockRequest()) {     //если по статусу блокирована
//	        throw new RuntimeException("No block request for this card");
//	    }
	    cardRepository.delete(card);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public Page<CardDto> getAllCards(Pageable pageable) {
	    return cardRepository.findAll(pageable)
	            .map(CardDto::fromEntity);
	}

}