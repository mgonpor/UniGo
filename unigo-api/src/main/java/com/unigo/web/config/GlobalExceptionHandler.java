package com.unigo.web.config;

import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.VehiculoException;
import com.unigo.service.exceptions.VehiculoNotFoundException;
import com.unigo.service.exceptions.ViajeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehiculoException.class)
    public ResponseEntity<?> handleVehiculoException(VehiculoException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VehiculoNotFoundException.class)
    public ResponseEntity<?> handleVehiculoNotFoundException(VehiculoNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ViajeException.class)
    public ResponseEntity<?> handleViajeException(ViajeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConductorNotFoundException.class)
    public ResponseEntity<?> handleConductorNotFoundException(ConductorNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
