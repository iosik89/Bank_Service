package com.example.bankcards.exception;

public class AccessDeniedCardException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public AccessDeniedCardException(String message) {
        super(message);
    }
}
