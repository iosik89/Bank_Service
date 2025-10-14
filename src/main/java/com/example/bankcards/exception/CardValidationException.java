package com.example.bankcards.exception;

public class CardValidationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CardValidationException(String message) {
        super(message);
    }
	
}
