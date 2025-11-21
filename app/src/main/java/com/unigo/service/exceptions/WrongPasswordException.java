package com.unigo.service.exceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
