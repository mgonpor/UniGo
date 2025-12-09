package com.unigo.auth;

import com.unigo.auth.dtos.AuthRequest;
import com.unigo.auth.dtos.AuthResponse;
import com.unigo.auth.dtos.RegisterRequest;
import com.unigo.security.config.JwtService;
import com.unigo.security.user.UsuarioRepository;
import com.unigo.service.exceptions.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        try{                                                            // todo: finales email
            if (request.getEmail() == null /*|| !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")*/){
                throw new IllegalArgumentException("Email invalido o vacío");
            }
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email ya registrado");
            }
            if (usuarioRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username ya registrado");
            }

            var user = AuthMapper.registerToUser(request);
            user.setPassword(encoder.encode(request.getPassword()));

            var savedUser = usuarioRepository.save(user);

            var jwtToken = jwtService.generateJwtToken(savedUser);
            return new AuthResponse(jwtToken);
        }catch (DataIntegrityViolationException e){
            if (e.getMessage().contains("email") || e.getMessage().contains("username")){
                throw new DuplicateResourceException("Email o username ya registrado");
            }
            throw new RuntimeException("Error de integridad de datos", e);
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
                                                    // todo: también con email
        var user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        var jwtToken = jwtService.generateJwtToken(user);
        return new AuthResponse(jwtToken);
    }

}
