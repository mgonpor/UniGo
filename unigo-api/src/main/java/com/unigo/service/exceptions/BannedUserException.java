package com.unigo.service.exceptions;

public class BannedUserException extends RuntimeException {
    public BannedUserException(String message) {
        super(message);
    }
}
