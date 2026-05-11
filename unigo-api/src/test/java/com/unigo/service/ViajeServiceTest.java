package com.unigo.service;

import com.unigo.persistence.entities.*;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.ViajeException;
import com.unigo.service.exceptions.ViajeNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViajeServiceTest {

    @Mock
    private ViajeRepository viajeRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ViajeService viajeService;

    private Viaje viaje;
    private Conductor conductor;
    private Usuario usuario;
    private Pasajero pasajero;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(viajeService, "precioMaximo", 20);

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");

        conductor = new Conductor();
        conductor.setId(1);
        conductor.setIdUsuario(1);

        pasajero = new Pasajero();
        pasajero.setId(1);
        pasajero.setIdUsuario(1);

        reserva = new Reserva();
        reserva.setId(1);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setPasajero(pasajero);

        viaje = new Viaje();
        viaje.setId(1);
        viaje.setIdConductor(1);
        viaje.setOrigen("Madrid");
        viaje.setDestino("Barcelona");
        viaje.setFechaSalida(LocalDate.now().plusDays(2));
        viaje.setPlazasDisponibles(3);
        viaje.setPrecioPlaza(15.0f);
        viaje.setEstadoViaje(EstadoViaje.DISPONIBLE);
        viaje.setConductor(conductor);
        List<Reserva> reservasList = new ArrayList<>();
        reservasList.add(reserva);
        viaje.setReservas(reservasList);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
    }

    @Test
    void findAll_Ok() {
        when(viajeRepository.findAll()).thenReturn(List.of(viaje));

        List<ViajeResponse> result = viajeService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(viaje.getId(), result.get(0).getId());
    }

    @Test
    void findById_Ok() {
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        ViajeResponse result = viajeService.findById(1);

        assertNotNull(result);
        assertEquals(viaje.getId(), result.getId());
    }

    @Test
    void findById_NotFound() {
        when(viajeRepository.existsById(1)).thenReturn(false);

        assertThrows(ViajeNotFoundException.class, () -> viajeService.findById(1));
    }

    @Test
    void createAdmin_Ok() {
        ViajeRequest request = new ViajeRequest();
        request.setOrigen("Madrid");
        request.setDestino("Valencia");
        request.setFechaSalida(LocalDate.now().plusDays(1));
        request.setPlazasDisponibles(4);
        request.setPrecioPorPlaza(10.0);

        when(conductorRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.createAdmin(1, request);

        assertNotNull(result);
        verify(viajeRepository, times(1)).save(any(Viaje.class));
    }

    @Test
    void createAdmin_ConductorNotFound() {
        ViajeRequest request = new ViajeRequest();
        when(conductorRepository.existsById(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> viajeService.createAdmin(1, request));
    }

    @Test
    void updateAdmin_Ok() {
        ViajeRequest request = new ViajeRequest();
        request.setId(1);
        request.setOrigen("Madrid");
        request.setDestino("Valencia");
        request.setFechaSalida(LocalDate.now().plusDays(1));
        request.setPlazasDisponibles(4);
        request.setPrecioPorPlaza(10.0);

        when(conductorRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.updateAdmin(1, 1, request);

        assertNotNull(result);
        verify(viajeRepository, times(1)).save(viaje);
    }

    @Test
    void updateAdmin_ConductorNotFound() {
        ViajeRequest request = new ViajeRequest();
        when(conductorRepository.existsById(1)).thenReturn(false);

        assertThrows(ConductorNotFoundException.class, () -> viajeService.updateAdmin(1, 1, request));
    }

    @Test
    void updateAdmin_IdMismatch() {
        ViajeRequest request = new ViajeRequest();
        request.setId(2);
        when(conductorRepository.existsById(1)).thenReturn(true);

        assertThrows(ViajeException.class, () -> viajeService.updateAdmin(1, 1, request));
    }

    @Test
    void updateAdmin_ViajeNotFound() {
        ViajeRequest request = new ViajeRequest();
        request.setId(1);
        when(conductorRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsByIdAndIdConductor(1, 1)).thenReturn(false);

        assertThrows(ViajeNotFoundException.class, () -> viajeService.updateAdmin(1, 1, request));
    }

    @Test
    void deleteAdmin_Ok() {
        when(viajeRepository.existsById(1)).thenReturn(true);

        String result = viajeService.deleteAdmin(1);

        assertEquals("Viaje 1 eliminado con exito", result);
        verify(viajeRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteAdmin_NotFound() {
        when(viajeRepository.existsById(1)).thenReturn(false);

        assertThrows(ViajeNotFoundException.class, () -> viajeService.deleteAdmin(1));
    }

    @Test
    void searchByEstado_Ok() {
        when(viajeRepository.findByEstadoViaje(EstadoViaje.DISPONIBLE)).thenReturn(List.of(viaje));

        List<ViajeResponse> result = viajeService.searchByEstado("DISPONIBLE");

        assertFalse(result.isEmpty());
    }

    @Test
    void searchByEstado_Invalid() {
        assertThrows(ViajeException.class, () -> viajeService.searchByEstado("INVALIDO"));
    }

    @Test
    void cambiarEstadoAdmin_Ok() {
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.cambiarEstadoAdmin(1, "COMPLETADO");

        assertNotNull(result);
        assertEquals(EstadoViaje.COMPLETADO, viaje.getEstadoViaje());
    }

    @Test
    void cambiarEstadoAdmin_NotFound() {
        when(viajeRepository.existsById(1)).thenReturn(false);

        assertThrows(ViajeNotFoundException.class, () -> viajeService.cambiarEstadoAdmin(1, "EN_CURSO"));
    }

    @Test
    void cambiarEstadoAdmin_InvalidEstado() {
        when(viajeRepository.existsById(1)).thenReturn(true);

        assertThrows(ViajeException.class, () -> viajeService.cambiarEstadoAdmin(1, "INVALIDO"));
    }

    @Test
    void getMisViajes_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.findAllByIdConductor(1)).thenReturn(List.of(viaje));

        List<ViajeResponse> result = viajeService.getMisViajes();

        assertFalse(result.isEmpty());
    }

    @Test
    void getMiViajeById_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.findByIdAndIdConductor(1, 1)).thenReturn(Optional.of(viaje));

        ViajeResponse result = viajeService.getMiViajeById(1);

        assertNotNull(result);
    }

    @Test
    void getMiViajeById_NotFound() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.findByIdAndIdConductor(1, 1)).thenReturn(Optional.empty());

        assertThrows(ViajeNotFoundException.class, () -> viajeService.getMiViajeById(1));
    }

    @Test
    void createViaje_Ok() {
        mockSecurityContext();
        ViajeRequest request = new ViajeRequest();
        request.setOrigen("Madrid");
        request.setDestino("Valencia");
        request.setFechaSalida(LocalDate.now().plusDays(1));
        request.setPlazasDisponibles(4);
        request.setPrecioPorPlaza(10.0);

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.createViaje(request);

        assertNotNull(result);
    }

    @Test
    void createViaje_FechaInvalida() {
        mockSecurityContext();
        ViajeRequest request = new ViajeRequest();
        request.setFechaSalida(LocalDate.now().minusDays(1));

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));

        assertThrows(ViajeException.class, () -> viajeService.createViaje(request));
    }

    @Test
    void createViaje_PrecioInvalido() {
        mockSecurityContext();
        ViajeRequest request = new ViajeRequest();
        request.setFechaSalida(LocalDate.now().plusDays(1));
        request.setPrecioPorPlaza(25.0); // Max is 20

        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));

        assertThrows(ViajeException.class, () -> viajeService.createViaje(request));
    }

    @Test
    void confirmarReserva_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        doNothing().when(reservaService).confirmarReservaDesdeViaje(1);
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.confirmarReserva(1, 1);

        assertNotNull(result);
        assertEquals(2, viaje.getPlazasDisponibles());
    }

    @Test
    void confimarReserva_ReservaInexistente() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        reserva.setId(2); // Diff ID

        assertThrows(ViajeException.class, () -> viajeService.confirmarReserva(1, 1));
    }

    @Test
    void cancelarReserva_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(viajeRepository.existsByIdAndIdConductor(1, 1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        doNothing().when(reservaService).cancelarReservaDesdeViaje(1);
        when(viajeRepository.save(any(Viaje.class))).thenReturn(viaje);

        ViajeResponse result = viajeService.cancelarReserva(1, 1);

        assertNotNull(result);
    }

    @Test
    void getReservasByIdViaje_Ok() {
        mockSecurityContext();
        when(conductorRepository.findByIdUsuario(1)).thenReturn(Optional.of(conductor));
        when(reservaService.getReservasByIdViaje(1)).thenReturn(List.of(new ReservaResponse()));

        List<ReservaResponse> result = viajeService.getReservasByIdViaje(1);

        assertFalse(result.isEmpty());
    }

    @Test
    void getViajeByIdPasajero_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(viajeRepository.findByIdAndEstadoViajeAndFechaSalidaAfter(eq(1), eq(EstadoViaje.DISPONIBLE),
                any(LocalDate.class)))
                .thenReturn(Optional.of(viaje));

        ViajeResponse result = viajeService.getViajeByIdPasajero(1);

        assertNotNull(result);
    }

    @Test
    void getViajeByIdPasajero_NotFound() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(viajeRepository.findByIdAndEstadoViajeAndFechaSalidaAfter(anyInt(), any(EstadoViaje.class),
                any(LocalDate.class)))
                .thenReturn(Optional.empty());

        assertThrows(ViajeException.class, () -> viajeService.getViajeByIdPasajero(1));
    }

    @Test
    void getMisViajesPasajero_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaService.getHistoricoViajesHechos(1)).thenReturn(List.of(1));
        when(viajeRepository.findAllByIdIn(List.of(1))).thenReturn(List.of(viaje));

        List<ViajeResponse> result = viajeService.getMisViajesPasajero();

        assertFalse(result.isEmpty());
    }

    @Test
    void getViajesDisponibles_Ok() {
        when(viajeRepository.findAllByEstadoViajeAndFechaSalidaAfter(eq(EstadoViaje.DISPONIBLE), any(LocalDate.class)))
                .thenReturn(List.of(viaje));

        List<ViajeResponse> result = viajeService.getViajesDisponibles();

        assertFalse(result.isEmpty());
    }

    @Test
    void puedeAccederAlChat_IsConductor() {
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        boolean result = viajeService.puedeAccederAlChat(1, 1); // Conductor is user 1

        assertTrue(result);
    }

    @Test
    void puedeAccederAlChat_IsPasajeroConReserva() {
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        boolean result = viajeService.puedeAccederAlChat(1, 1);

        assertTrue(result);
    }

    @Test
    void puedeAccederAlChat_IsPasajeroSinReservaConfirmada() {
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        boolean result = viajeService.puedeAccederAlChat(2, 1);

        assertFalse(result); // user 1 not driver (if changed) or not confirmed
    }
}
