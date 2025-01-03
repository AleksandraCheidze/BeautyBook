package com.example.end.dto;


public class ErrorResponse {
    private String message;

    // Конструктор
    public ErrorResponse(String message) {
        this.message = message;
    }

    // Геттер
    public String getMessage() {
        return message;
    }

    // Сеттер
    public void setMessage(String message) {
        this.message = message;
    }
}
