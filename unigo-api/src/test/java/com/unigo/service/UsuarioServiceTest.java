package com.unigo.service;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.RegisterRequest;
import com.unigo.service.exceptions.UsuarioException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");
        usuario.setPassword("password");
        usuario.setRol("USER");
    }

    @Test
    void loadUserByUsername_Ok() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_NotFound() {
        when(usuarioRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("unknown"));
    }

    @Test
    void create_Ok() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setEmail("test@test.com");
        request.setNombre("Test Name");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario createdUsuario = usuarioService.create(request);

        assertNotNull(createdUsuario);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void findByUsername_Ok() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsername_NotFound() {
        when(usuarioRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.findByUsername("unknown"));
    }

    @Test
    void crearUsuarioComoAdmin_Ok() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminuser");
        request.setPassword("password");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String result = usuarioService.crearUsuarioComoAdmin(request);

        assertEquals("Usuario adminuser ha sido creado correctamente.", result);
    }

    @Test
    void crearUsuarioComoAdmin_Exception() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminuser");
        request.setPassword("password");

        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("DB Error"));

        assertThrows(UsuarioException.class, () -> usuarioService.crearUsuarioComoAdmin(request));
    }

    @Test
    void cambiarRolUsuario_Ok() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String result = usuarioService.cambiarRolUsuario(1, "ADMIN");

        assertEquals("Nuevo rol: ADMIN", result);
        assertEquals("ADMIN", usuario.getRol());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void cambiarRolUsuario_NotFound() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.cambiarRolUsuario(1, "ADMIN"));
    }
}
