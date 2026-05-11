package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConductorServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConductorService conductorService;

    private Conductor conductor;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");

        conductor = new Conductor();
        conductor.setId(1);
        conductor.setIdUsuario(1);
        conductor.setReputacion(4.5f);
        conductor.setNumValoraciones(10);
        conductor.setUsuario(usuario);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAll_ShouldReturnList() {
        when(conductorRepository.findAll()).thenReturn(List.of(conductor));

        List<ConductorResponse> result = conductorService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(conductor.getId(), result.get(0).getId());
    }

    @Test
    void findByIdUsuario_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));

        ConductorResponse result = conductorService.findByIdUsuario(1);

        assertNotNull(result);
        assertEquals(conductor.getId(), result.getId());
    }

    @Test
    void findByIdUsuario_UsuarioNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> conductorService.findByIdUsuario(1));
    }

    @Test
    void findByIdUsuario_ConductorNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.empty());

        assertThrows(ConductorNotFoundException.class, () -> conductorService.findByIdUsuario(1));
    }

    @Test
    void create_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdUsuario(1)).thenReturn(false);
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        ConductorResponse result = conductorService.create(1);

        assertNotNull(result);
        assertEquals(conductor.getId(), result.getId());
        verify(conductorRepository, times(1)).save(any(Conductor.class));
    }

    @Test
    void updateReputacion_Ok() {
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdAndIdUsuario(1, 1)).thenReturn(true);
        when(conductorRepository.findById(1)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        ConductorResponse result = conductorService.updateReputacion(1, 1, 3.5f);

        assertNotNull(result);
        assertEquals(3.5f, conductor.getReputacion());
        verify(conductorRepository, times(1)).save(conductor);
    }

    @Test
    void updateReputacion_ConductorNotExists() {
        when(conductorRepository.existsById(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> conductorService.updateReputacion(1, 1, 3.5f));
    }

    @Test
    void updateReputacion_NotMatchingUsuario() {
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdAndIdUsuario(1, 2)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> conductorService.updateReputacion(2, 1, 3.5f));
    }

    @Test
    void updateReputacion_InvalidReputacionLow() {
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdAndIdUsuario(1, 1)).thenReturn(true);
        when(conductorRepository.findById(1)).thenReturn(Optional.of(conductor));

        assertThrows(ConductorException.class, () -> conductorService.updateReputacion(1, 1, -1f));
    }

    @Test
    void updateReputacion_InvalidReputacionHigh() {
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdAndIdUsuario(1, 1)).thenReturn(true);
        when(conductorRepository.findById(1)).thenReturn(Optional.of(conductor));

        assertThrows(ConductorException.class, () -> conductorService.updateReputacion(1, 1, 5.1f));
    }

    @Test
    void delete_Ok() {
        when(conductorRepository.existsByIdUsuario(1)).thenReturn(true);
        doNothing().when(conductorRepository).deleteByIdUsuario(1);

        String result = conductorService.delete(1);

        assertEquals("Conductor con id de usuario 1 eliminado.", result);
        verify(conductorRepository, times(1)).deleteByIdUsuario(1);
    }

    @Test
    void delete_NotFound() {
        when(conductorRepository.existsByIdUsuario(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> conductorService.delete(1));
    }

    @Test
    void getMeConductor_Ok() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(conductorRepository.findByUsuario_Username("testuser")).thenReturn(Optional.of(conductor));

        ConductorResponse result = conductorService.getMeConductor();

        assertNotNull(result);
        assertEquals(conductor.getId(), result.getId());
    }

    @Test
    void getMeConductor_NotFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(conductorRepository.findByUsuario_Username("testuser")).thenReturn(Optional.empty());

        assertThrows(ConductorNotFoundException.class, () -> conductorService.getMeConductor());
    }

    @Test
    void autoCreate_Ok() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdUsuario(1)).thenReturn(false);
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        Conductor result = conductorService.autoCreate(1);

        assertNotNull(result);
        assertEquals(conductor.getId(), result.getId());
    }

    @Test
    void autoCreate_UsuarioNotFound() {
        when(usuarioRepository.existsById(1)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> conductorService.autoCreate(1));
    }

    @Test
    void autoCreate_Duplicate() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        when(conductorRepository.existsByIdUsuario(1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> conductorService.autoCreate(1));
    }

    @Test
    void findByReputacionGreaterThanEqual_Ok() {
        when(conductorRepository.findByReputacionGreaterThanEqual(4.0f)).thenReturn(List.of(conductor));

        List<ConductorResponse> result = conductorService.findByReputacionGreaterThanEqual(4.0f);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByReputacionGreaterThanEqual_InvalidReputacionLow() {
        assertThrows(ConductorException.class, () -> conductorService.findByReputacionGreaterThanEqual(-1.0f));
    }

    @Test
    void findByReputacionGreaterThanEqual_InvalidReputacionHigh() {
        assertThrows(ConductorException.class, () -> conductorService.findByReputacionGreaterThanEqual(6.0f));
    }

    @Test
    void findByReputacionGreaterThanEqual_NotFound() {
        when(conductorRepository.findByReputacionGreaterThanEqual(4.0f)).thenReturn(Collections.emptyList());

        assertThrows(ConductorNotFoundException.class, () -> conductorService.findByReputacionGreaterThanEqual(4.0f));
    }
}
