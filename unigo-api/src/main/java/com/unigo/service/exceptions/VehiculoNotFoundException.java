package com.unigo.service.exceptions;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(String message) {
        super(message);
    }
}
