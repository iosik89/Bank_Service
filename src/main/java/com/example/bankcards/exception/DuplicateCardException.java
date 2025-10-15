package com.example.bankcards.exception;

public class DuplicateCardException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateCardException(String message) {
        super(message);
    }
}
