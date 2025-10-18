package com.example.bankcards.exception;

public class AdminCardCreationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public AdminCardCreationException(String message) {
        super(message);
    }
}
