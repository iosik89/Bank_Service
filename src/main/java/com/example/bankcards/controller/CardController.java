package com.example.bankcards.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/cards")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CardController {
	
	private final CardService cardService;
	private final UserRepository userRepository;
	
	/*
	 * Отображает все карты текущего авторизованного пользователя
	 * */
    @GetMapping("/userCards")
    public ResponseEntity<Page<CardDto>> getMyCards(
            Authentication authentication,
            @ModelAttribute CardSearchRequest searchRequest
    ) {
	      User user = userRepository.findByUsername(authentication.getName())
	    			.orElseThrow(() -> new RuntimeException("Пользователь не найден"));
	      Long userId = user.getId();
          Page<CardDto> cards = cardService.getUserCards(userId, searchRequest);
          return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/allCards")
    public ResponseEntity<Page<CardDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size   
    ) {
    	  PageRequest pageable = PageRequest.of(page, size);
          Page<CardDto> cards = cardService.getAllCards(pageable);
          return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        BigDecimal balance = cardService.getCardBalance(id, user.getId());
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<CardDto>> searchMyCards(
            @RequestParam String query,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cardService.searchUserCards(user.getId(), query, pageable));
    }
	
	@PostMapping("/create")
	public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto dto) {
	    Card card = cardService.createCard(dto);
	    return ResponseEntity.ok(CardDto.fromEntity(card));
	}
	
	@DeleteMapping("/{cardId}/delete")
	public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
	    cardService.deleteCard(cardId);
	    return ResponseEntity.ok("Card delete");
	}
	
	@PostMapping("/transfer")
	public ResponseEntity<String> transferBetweenOwnCards(
			@RequestParam Long fromCardId,
            @RequestParam Long toCardId,
            @RequestParam BigDecimal amount,
            Authentication authentication) { //ищем пользователя
	    String username = authentication.getName();
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

	    cardService.transferBetweenOwnCards(user.getId(), fromCardId, toCardId, amount);
	    return ResponseEntity.ok("Перевод выполнен успешно");
	}
	
    @PostMapping("/{cardId}/request-block")
    public ResponseEntity<String> requestBlock(@PathVariable Long cardId,Authentication authentication) {
    	User user = userRepository.findByUsername(authentication.getName())
    			.orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    	Long userId = user.getId();
    	cardService.requestBlockCard(userId, cardId);
		return ResponseEntity.ok("Блокировка карты запрошена");	
	}
    
    @PostMapping("/{cardId}/block")
    public ResponseEntity<String> blockCard(@PathVariable Long cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok("Card blocked");
    }
	
	
}
