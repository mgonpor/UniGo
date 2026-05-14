package com.unigo.service;

import com.unigo.persistence.entities.Usuario;
import com.unigo.service.dtos.LoginRequest;
import com.unigo.service.dtos.LoginResponse;
import com.unigo.service.dtos.RefreshDTO;
import com.unigo.service.dtos.RegisterRequest;
import com.unigo.service.exceptions.BannedUserException;
import com.unigo.web.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired  //Para crear pasajero automáticamente
    private PasajeroService pasajeroService;

    public LoginResponse registrar(RegisterRequest request) {
        Usuario u = this.usuarioService.create(request);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken  = jwtUtil.generateAccessToken(userDetails, u.getId());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, u.getId());

        this.pasajeroService.autoCreate(u.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario u = this.usuarioService.findByUsername(request.getUsername());

        if(u.isBaneado()){
            throw new BannedUserException("Este usuario está baneado");
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails, u.getId());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, u.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refresh(RefreshDTO dto) {
        String accessToken = jwtUtil.generateAccessToken(dto.getRefresh());
        String refreshToken = jwtUtil.generateRefreshToken(dto.getRefresh());

        return new LoginResponse(accessToken, refreshToken);
    }
}
