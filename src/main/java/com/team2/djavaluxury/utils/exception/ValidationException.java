package com.team2.djavaluxury.utils.exception;

public class ValidationException extends RuntimeException {
    public ValidationException (String message, Throwable cause) {
        super(message, cause);
    }
}