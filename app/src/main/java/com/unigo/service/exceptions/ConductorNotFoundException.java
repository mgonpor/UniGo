package com.unigo.service.exceptions;

public class ConductorNotFoundException extends RuntimeException {
    public ConductorNotFoundException(String message) {
        super(message);
    }
}
