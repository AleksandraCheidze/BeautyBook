package com.example.end.infrastructure.exceptions;

public class ProcedureAlreadyExistsException extends RuntimeException {
    public ProcedureAlreadyExistsException(String message) {
        super(message);
    }
} 