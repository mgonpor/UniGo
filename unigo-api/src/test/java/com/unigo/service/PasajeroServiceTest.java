package com.unigo.service;

import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.PasajeroResponse;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.PasajeroNotFoundException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasajeroServiceTest {

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PasajeroService pasajeroService;

    private Pasajero pasajero;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);

        pasajero = new Pasajero();
        pasajero.setId(1);
        pasajero.setIdUsuario(1);
        pasajero.setUsuario(usuario);
    }

    @Test
    void getPasajeros_Ok() {
        when(pasajeroRepository.findAll()).thenReturn(List.of(pasajero));

        List<PasajeroResponse> result = pasajeroService.getPasajeros();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pasajero.getId(), result.get(0).getId());
    }

    @Test
    void createPasajero_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsByIdUsuario(1)).thenReturn(false);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        PasajeroResponse result = pasajeroService.createPasajero(1);

        assertNotNull(result);
        assertEquals(pasajero.getId(), result.getId());
        verify(pasajeroRepository, times(1)).save(any(Pasajero.class));
    }

    @Test
    void createPasajero_UsuarioNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> pasajeroService.createPasajero(1));
    }

    @Test
    void createPasajero_DuplicateResource() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsByIdUsuario(1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> pasajeroService.createPasajero(1));
    }

    @Test
    void getPasajeroByIdUsuario_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));

        PasajeroResponse result = pasajeroService.getPasajeroByIdUsuario(1);

        assertNotNull(result);
        assertEquals(pasajero.getId(), result.getId());
    }

    @Test
    void getPasajeroByIdUsuario_UsuarioNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> pasajeroService.getPasajeroByIdUsuario(1));
    }

    @Test
    void getPasajeroByIdUsuario_PasajeroNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.empty());

        assertThrows(PasajeroNotFoundException.class, () -> pasajeroService.getPasajeroByIdUsuario(1));
    }

    @Test
    void autoCreate_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsByIdUsuario(1)).thenReturn(false);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        assertDoesNotThrow(() -> pasajeroService.autoCreate(1));
        verify(pasajeroRepository, times(1)).save(any(Pasajero.class));
    }

    @Test
    void autoCreate_UsuarioNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> pasajeroService.autoCreate(1));
    }

    @Test
    void autoCreate_DuplicateResource() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsByIdUsuario(1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> pasajeroService.autoCreate(1));
    }
}
