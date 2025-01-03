package com.example.end.exceptions;

public class ImageDeleteException extends RuntimeException {
    public ImageDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
