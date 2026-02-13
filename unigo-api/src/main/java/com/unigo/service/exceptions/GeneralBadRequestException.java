package com.unigo.service.exceptions;

public class GeneralBadRequestException extends RuntimeException {
    public GeneralBadRequestException(String message) {
        super(message);
    }
}
