package com.unigo.service.exceptions;

public class ViajeNotFoundException extends RuntimeException{

    static final long serialVersionUID = 1L;

    public ViajeNotFoundException(String message) {
        super(message);
    }

}
