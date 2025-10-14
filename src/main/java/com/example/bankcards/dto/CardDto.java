package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Card.CardStatus;
import com.example.bankcards.util.CardUtil;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CardDto {
	
	private Long id;
	
	@NotBlank(message = "Номер карты не может быть пустым")
    @Pattern(
        regexp = "^(\\d{4} \\d{4} \\d{4} \\d{4})$",
        message = "Номер карты должен содержать 16 цифр, "
        		+ "разделённых пробелами (например: 1234 5678 9012 3456)"
    )
	private String pan; // **** **** **** 1234
	private String holderName;
	private LocalDate expirationDate;
	private CardStatus status;
	
    @NotNull(message = "Баланс обязателен")
    @DecimalMin(value = "0.00", message = "Баланс не может быть отрицательным")
	private BigDecimal balance;
	
	public static CardDto fromEntity(Card card) {
	    CardDto dto = new CardDto();
	    dto.id = card.getId();
	    dto.pan = CardUtil.maskCardNumber(card.getPan()); // маска **** **** **** + 4last
	    dto.holderName = card.getHolderName();
	    dto.expirationDate = card.getExpirationDate();
	    dto.status = card.getStatus();
	    dto.balance = card.getBalance();
	    return dto;
	}
}
