package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Vehiculo;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.VehiculoRepository;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.VehiculoException;
import com.unigo.service.exceptions.VehiculoNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConductorService conductorService;

    @InjectMocks
    private VehiculoService vehiculoService;

    private Vehiculo vehiculo;
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

        vehiculo = new Vehiculo();
        vehiculo.setId(1);
        vehiculo.setIdConductor(1);
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");
        vehiculo.setColor("Rojo");
        vehiculo.setMatricula("1234ABC");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
    }

    @Test
    void findAll_Ok() {
        when(vehiculoRepository.findAll()).thenReturn(List.of(vehiculo));

        List<VehiculoResponse> result = vehiculoService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(vehiculo.getId(), result.get(0).getId());
    }

    @Test
    void findById_Ok() {
        when(vehiculoRepository.existsById(1)).thenReturn(true);
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));

        VehiculoResponse result = vehiculoService.findById(1);

        assertNotNull(result);
        assertEquals(vehiculo.getId(), result.getId());
    }

    @Test
    void findById_NotFound() {
        when(vehiculoRepository.existsById(1)).thenReturn(false);

        assertThrows(VehiculoNotFoundException.class, () -> vehiculoService.findById(1));
    }

    @Test
    void createAdmin_Ok() {
        VehiculoRequest request = new VehiculoRequest();
        request.setMarca("Ford");
        request.setModelo("Focus");
        request.setColor("Azul");
        request.setMatricula("9876ZYX");

        when(conductorRepository.existsById(1)).thenReturn(true);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        VehiculoResponse result = vehiculoService.createAdmin(1, request);

        assertNotNull(result);
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void createAdmin_ConductorNotFound() {
        VehiculoRequest request = new VehiculoRequest();
        when(conductorRepository.existsById(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> vehiculoService.createAdmin(1, request));
    }

    @Test
    void updateAdmin_Ok() {
        VehiculoRequest request = new VehiculoRequest();
        request.setId(1);
        request.setMarca("Ford");
        request.setModelo("Focus");

        when(conductorRepository.existsById(1)).thenReturn(true);
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));

        VehiculoResponse result = vehiculoService.updateAdmin(1, 1, request);

        assertNotNull(result);
        verify(vehiculoRepository, times(1)).save(vehiculo);
    }

    @Test
    void updateAdmin_ConductorNotFound() {
        VehiculoRequest request = new VehiculoRequest();
        when(conductorRepository.existsById(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> vehiculoService.updateAdmin(1, 1, request));
    }

    @Test
    void updateAdmin_IdMismatch() {
        VehiculoRequest request = new VehiculoRequest();
        request.setId(2);
        when(conductorRepository.existsById(1)).thenReturn(true);

        assertThrows(VehiculoException.class, () -> vehiculoService.updateAdmin(1, 1, request));
    }

    @Test
    void updateAdmin_VehiculoNotFound() {
        VehiculoRequest request = new VehiculoRequest();
        request.setId(1);
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(false);

        assertThrows(VehiculoNotFoundException.class, () -> vehiculoService.updateAdmin(1, 1, request));
    }

    @Test
    void deleteAdmin_Ok() {
        when(vehiculoRepository.existsById(1)).thenReturn(true);

        String result = vehiculoService.deleteAdmin(1);

        assertEquals("Vehiculo 1 eliminado", result);
        verify(vehiculoRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteAdmin_NotFound() {
        when(vehiculoRepository.existsById(1)).thenReturn(false);

        assertThrows(VehiculoNotFoundException.class, () -> vehiculoService.deleteAdmin(1));
    }

    @Test
    void getMisVehiculos_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findAllByIdConductor(1)).thenReturn(List.of(vehiculo));

        List<VehiculoResponse> result = vehiculoService.getMisVehiculos();

        assertFalse(result.isEmpty());
    }

    @Test
    void getMisVehiculos_NotConductor() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.empty());

        assertThrows(ConductorNotFoundException.class, () -> vehiculoService.getMisVehiculos());
    }

    @Test
    void getVehiculoById_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));

        VehiculoResponse result = vehiculoService.getVehiculoById(1);

        assertNotNull(result);
    }

    @Test
    void getVehiculoById_NotFound() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(false);

        assertThrows(VehiculoNotFoundException.class, () -> vehiculoService.getVehiculoById(1));
    }

    @Test
    void createVehiculo_Ok() {
        mockSecurityContext();
        VehiculoRequest request = new VehiculoRequest();
        request.setMarca("Seat");
        request.setModelo("Leon");
        request.setColor("Blanco");
        request.setMatricula("1234BCD");

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        VehiculoResponse result = vehiculoService.createVehiculo(request);

        assertNotNull(result);
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void createVehiculo_InvalidMatricula() {
        mockSecurityContext();
        VehiculoRequest request = new VehiculoRequest();
        request.setMatricula("INVALIDA");

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));

        assertThrows(VehiculoException.class, () -> vehiculoService.createVehiculo(request));
    }

    @Test
    void updateVehiculo_Ok() {
        mockSecurityContext();
        VehiculoRequest request = new VehiculoRequest();
        request.setId(1);
        request.setMarca("Seat");

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        VehiculoResponse result = vehiculoService.updateVehiculo(1, request);

        assertNotNull(result);
        verify(vehiculoRepository, times(1)).save(vehiculo);
    }

    @Test
    void updateVehiculo_IdMismatch() {
        VehiculoRequest request = new VehiculoRequest();
        request.setId(2);

        assertThrows(VehiculoException.class, () -> vehiculoService.updateVehiculo(1, request));
    }

    @Test
    void delete_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);

        String result = vehiculoService.delete(1);

        assertEquals("Vehiculo 1 eliminado con éxito.", result);
        verify(vehiculoRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_NotFound() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.existsByIdAndIdConductor(1, 1)).thenReturn(false);

        assertThrows(VehiculoNotFoundException.class, () -> vehiculoService.delete(1));
    }
}
