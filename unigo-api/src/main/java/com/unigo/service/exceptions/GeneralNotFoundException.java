package com.unigo.service.exceptions;

public class GeneralNotFoundException extends RuntimeException {
    public GeneralNotFoundException(String message) {
        super(message);
    }
}
