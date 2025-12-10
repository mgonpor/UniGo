package com.unigo.auth;

import com.unigo.auth.dtos.AuthRequest;
import com.unigo.auth.dtos.RegisterRequest;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // todo: crear GeneralExceptionHandler con @RestControllerAdvice (investigar)
    // Necesario para cuando @Valid -> MethodArgumentNotValidException

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        }catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UsernameNotFoundException | UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + " : " + e.getCause().getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        }catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
