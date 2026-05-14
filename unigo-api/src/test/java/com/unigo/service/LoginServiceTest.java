package com.unigo.service;

import com.unigo.persistence.entities.Usuario;
import com.unigo.service.dtos.LoginRequest;
import com.unigo.service.dtos.LoginResponse;
import com.unigo.service.dtos.RefreshDTO;
import com.unigo.service.dtos.RegisterRequest;
import com.unigo.web.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtil;

    @Mock
    private PasajeroService pasajeroService;

    @InjectMocks
    private LoginService loginService;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = new User("testuser", "password", Collections.emptyList());
        authentication = mock(Authentication.class);
    }

    @Test
    void registrar_Ok() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");

        when(usuarioService.create(any(RegisterRequest.class))).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(eq(userDetails), eq(1))).thenReturn("mocked-access-token");
        when(pasajeroService.autoCreate(1)).thenReturn(null);

        String token = String.valueOf(loginService.registrar(request));

        assertEquals("mocked-access-token", token);
        verify(usuarioService, times(1)).create(request);
        verify(pasajeroService, times(1)).autoCreate(1);
        verify(jwtUtil, times(1)).generateAccessToken(userDetails, 1);
    }

    @Test
    void login_Ok() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioService.findByUsername("testuser")).thenReturn(usuario);
        when(jwtUtil.generateAccessToken(eq(userDetails), eq(1))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(eq(userDetails), eq(1))).thenReturn("refresh-token");

        LoginResponse response = loginService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccess());
        assertEquals("refresh-token", response.getRefresh());
    }

    @Test
    void refresh_Ok() {
        RefreshDTO request = new RefreshDTO();
        request.setRefresh("old-refresh-token");

        when(jwtUtil.generateAccessToken(anyString())).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("new-refresh-token");

        LoginResponse response = loginService.refresh(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccess());
        assertEquals("new-refresh-token", response.getRefresh());
    }
}
