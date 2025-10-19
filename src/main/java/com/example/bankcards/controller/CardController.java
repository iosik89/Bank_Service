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
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;

@Slf4j
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


    /**
     * Возвращает страницы карт текущего пользователя.
     * Если не переданы параметры поиска — вернёт все карты (постранично).
     * Параметры поиска: query, panLast4, status.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<CardDto>> getMyCards(
            Authentication authentication,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String panLast4,
            @RequestParam(required = false) Card.CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        // Если ни один фильтр не передан — вернём все карты
        if ((query == null || query.isBlank()) && (panLast4 == null || panLast4.isBlank()) && status == null) {
            Page<CardDto> cards = cardService.getUserCards(user.getId(), page, size);
            return ResponseEntity.ok(cards);
        }
        
        // Иначе — поиск по переданным параметрам
        Page<CardDto> cards = cardService.searchUserCards(user.getId(), query, panLast4, status, page, size);
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
    /***************************************************************************/
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
    		 @Parameter( description = "ID карты")
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
		    @Parameter(
		            description = "ID карты, с которой списываются деньги",
		            example = "1")
			@RequestParam Long fromCardId,
			 @Parameter(
			            description = "ID карты, на которую зачисляются деньги",
			            example = "2"
			        )
            @RequestParam Long toCardId,
            @Parameter(
                    description = "Сумма перевода в рублях",
                    example = "500.00"
                )
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
