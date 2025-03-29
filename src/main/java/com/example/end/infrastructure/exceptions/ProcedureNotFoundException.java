package com.example.end.infrastructure.exceptions;

public class ProcedureNotFoundException extends RuntimeException {

    public ProcedureNotFoundException(String message) {
        super(message);
    }


}
