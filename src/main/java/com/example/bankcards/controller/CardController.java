package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cards")
@SecurityRequirement(name = "bearerAuth")
public class CardController {
	
	private final CardService cardService;
	
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
	
    
	@GetMapping("/{id}")
	public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
	    Card card = cardService.getById(id);
	    return ResponseEntity.ok(CardDto.fromEntity(card));
	}
	
    
	@PostMapping("/create")
	public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto dto) {
		System.out.println("Received Card DTO: " + dto);
	    Card card = cardService.createCard(dto);
	    return ResponseEntity.ok(CardDto.fromEntity(card));
	}
}
