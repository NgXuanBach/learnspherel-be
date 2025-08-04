package com.learnspherel.exception;

public class PasswordResetTokenInvalidException extends RuntimeException {
    public PasswordResetTokenInvalidException(String message) {
        super(message);
    }
}