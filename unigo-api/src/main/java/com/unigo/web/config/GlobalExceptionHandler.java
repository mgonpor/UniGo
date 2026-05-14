package com.unigo.web.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.unigo.service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ─── 400 Bad Request ─────────────────────────────────────────────
    @ExceptionHandler({
            VehiculoException.class,
            ViajeException.class,
            ReservaException.class,
            ConductorException.class,
            UsuarioException.class,
            GeneralBadRequestException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e) {
        return body(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // ─── 401 Unauthorized (token invalido / no autenticado) ──────────
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<?> handleJwt(JWTVerificationException e) {
        return body(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e) {
        return body(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException e) {
        return body(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    // ─── 403 Forbidden ───────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        return body(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(BannedUserException.class)
    public ResponseEntity<?> handleBannedUser(BannedUserException e){
        return body(HttpStatus.FORBIDDEN, e.getMessage());
    }

    // ─── 404 Not Found ───────────────────────────────────────────────
    @ExceptionHandler({
            VehiculoNotFoundException.class,
            ViajeNotFoundException.class,
            ReservaNotFoundException.class,
            ConductorNotFoundException.class,
            PasajeroNotFoundException.class,
            UsuarioNotFoundException.class,
            GeneralNotFoundException.class
    })
    public ResponseEntity<?> handleNotFound(Exception e) {
        return body(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // Endpoint inexistente: lo manda Spring cuando no hay handler/recurso.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResource(NoResourceFoundException e) {
        return body(HttpStatus.NOT_FOUND, "Endpoint no encontrado");
    }

    // ─── 409 Conflict (duplicados) ───────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateResourceException e) {
        return body(HttpStatus.CONFLICT, e.getMessage());
    }

    // ─── 500 fallback ────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> body(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, status);
    }
}
