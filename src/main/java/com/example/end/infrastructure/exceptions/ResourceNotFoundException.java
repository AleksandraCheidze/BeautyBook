package com.example.end.infrastructure.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, Long id) {
        super(String.format("%s with id %d not found", entity, id));
    }

    public ResourceNotFoundException(String entity, String identifier) {
        super(String.format("%s with identifier %s not found", entity, identifier));
    }
}