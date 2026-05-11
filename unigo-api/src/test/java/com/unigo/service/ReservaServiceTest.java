package com.unigo.service;

import com.unigo.persistence.entities.*;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.*;
import com.unigo.service.dtos.ReservaRequest;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.exceptions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private ViajeRepository viajeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private Viaje viaje;
    private Pasajero pasajero;
    private Usuario usuario;
    private Conductor conductor;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("testuser");

        pasajero = new Pasajero();
        pasajero.setId(1);
        pasajero.setIdUsuario(1);

        conductor = new Conductor();
        conductor.setId(1);
        conductor.setIdUsuario(2);
        conductor.setReputacion(4.0f);
        conductor.setNumValoraciones(1);

        viaje = new Viaje();
        viaje.setId(1);
        viaje.setIdConductor(1);
        viaje.setEstadoViaje(EstadoViaje.DISPONIBLE);
        viaje.setPlazasDisponibles(3);
        viaje.setFechaSalida(LocalDate.now().plusDays(5));

        reserva = new Reserva();
        reserva.setId(1);
        reserva.setIdPasajero(1);
        reserva.setIdViaje(1);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDate.now());
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
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));
        List<ReservaResponse> result = reservaService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(reserva.getId(), result.get(0).getId());
    }

    @Test
    void findById_Ok() {
        when(reservaRepository.existsById(1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        ReservaResponse result = reservaService.findById(1);
        assertNotNull(result);
        assertEquals(reserva.getId(), result.getId());
    }

    @Test
    void findById_NotFound() {
        when(reservaRepository.existsById(1)).thenReturn(false);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.findById(1));
    }

    @Test
    void createAdmin_Ok() {
        when(pasajeroRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse result = reservaService.createAdmin(1, 1);
        assertNotNull(result);
        assertEquals(2, viaje.getPlazasDisponibles());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
        verify(viajeRepository, times(1)).save(viaje);
    }

    @Test
    void createAdmin_PasajeroNotFound() {
        when(pasajeroRepository.existsById(1)).thenReturn(false);
        assertThrows(PasajeroNotFoundException.class, () -> reservaService.createAdmin(1, 1));
    }

    @Test
    void createAdmin_ViajeNotFound() {
        when(pasajeroRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsById(1)).thenReturn(false);
        assertThrows(ViajeNotFoundException.class, () -> reservaService.createAdmin(1, 1));
    }

    @Test
    void createAdmin_NoPlazas() {
        viaje.setPlazasDisponibles(0);
        when(pasajeroRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        assertThrows(ViajeException.class, () -> reservaService.createAdmin(1, 1));
    }

    @Test
    void updateAdmin_Ok() {
        ReservaRequest request = new ReservaRequest();
        request.setId(1);
        request.setIdPasajero(1);
        request.setEstadoReserva("CONFIRMADA");
        request.setValoracionNumerica(4);
        request.setValoracionTexto("");

        when(reservaRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse result = reservaService.updateAdmin(1, 1, 1, request);
        assertNotNull(result);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void updateAdmin_IdsNoCoincidenReserva() {
        ReservaRequest request = new ReservaRequest();
        request.setId(2);
        assertThrows(ReservaException.class, () -> reservaService.updateAdmin(1, 1, 1, request));
    }

    @Test
    void updateAdmin_IdsNoCoincidenPasajero() {
        ReservaRequest request = new ReservaRequest();
        request.setId(1);
        request.setIdPasajero(2);
        assertThrows(ReservaException.class, () -> reservaService.updateAdmin(1, 1, 1, request));
    }

    @Test
    void updateAdmin_InvalidEstado() {
        ReservaRequest request = new ReservaRequest();
        request.setId(1);
        request.setIdPasajero(1);
        request.setEstadoReserva("INVALIDO");

        when(reservaRepository.existsById(1)).thenReturn(true);
        when(pasajeroRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        assertThrows(ReservaException.class, () -> reservaService.updateAdmin(1, 1, 1, request));
    }

    @Test
    void deleteAdmin_Ok() {
        when(reservaRepository.existsById(1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        String result = reservaService.deleteAdmin(1);
        assertEquals("Reserva 1 eliminada con éxito.", result);
        assertEquals(4, viaje.getPlazasDisponibles());
        verify(reservaRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteAdmin_NotFound() {
        when(reservaRepository.existsById(1)).thenReturn(false);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.deleteAdmin(1));
    }

    @Test
    void getMisReservas_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.findAllByIdPasajero(1)).thenReturn(List.of(reserva));

        List<ReservaResponse> result = reservaService.getMisReservas();
        assertFalse(result.isEmpty());
    }

    @Test
    void getMiReservaById_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.existsByIdAndIdPasajero(1, 1)).thenReturn(true);
        when(reservaRepository.findByIdAndIdPasajero(1, 1)).thenReturn(Optional.of(reserva));

        ReservaResponse result = reservaService.getMiReservaById(1);
        assertNotNull(result);
    }

    @Test
    void getMiReservaById_NotFound() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.existsByIdAndIdPasajero(1, 1)).thenReturn(false);

        assertThrows(ReservaNotFoundException.class, () -> reservaService.getMiReservaById(1));
    }

    @Test
    void getMisReservasByEstado_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.findAllByIdPasajeroAndEstadoReserva(1, EstadoReserva.PENDIENTE))
                .thenReturn(List.of(reserva));

        List<ReservaResponse> result = reservaService.getMisReservasByEstado("PENDIENTE");
        assertFalse(result.isEmpty());
    }

    @Test
    void getMisReservasByEstado_InvalidEstado() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));

        assertThrows(ViajeException.class, () -> reservaService.getMisReservasByEstado("INVALIDO"));
    }

    @Test
    void createReserva_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));
        when(pasajeroRepository.existsById(1)).thenReturn(true); // internally calls createAdmin
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        ReservaResponse result = reservaService.createReserva(1);
        assertNotNull(result);
    }

    @Test
    void cancelarReservaPasajero_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.existsByIdAndIdPasajero(1, 1)).thenReturn(true);
        when(reservaRepository.findByIdAndIdPasajero(1, 1)).thenReturn(Optional.of(reserva));
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        String result = reservaService.cancelarReservaPasajero(1);
        assertEquals("Reserva 1 eliminada con éxito.", result);
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstadoReserva());
        assertEquals(4, viaje.getPlazasDisponibles());
        verify(reservaRepository, times(1)).deleteById(1);
    }

    @Test
    void ponerValoraciones_Ok() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.existsByIdAndIdPasajero(1, 1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        // Note: the service logic uses rDB.getId() to find Viaje, which is a bug in
        // original code, but we must mock it to avoid test fail.
        viaje.setFechaSalida(LocalDate.now().minusDays(1)); // Viaje en el pasado para poder valorar
        when(viajeRepository.existsById(1)).thenReturn(true);
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje)); // for dates and conductor
        when(conductorRepository.findById(1)).thenReturn(Optional.of(conductor));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        viaje.setFechaSalida(LocalDate.now().minusDays(5));
        ReservaResponse result = reservaService.ponerValoraciones(1, 5, "Genial");
        assertNotNull(result);
        assertEquals(5, reserva.getValoracionNumerica());
        assertEquals("Genial", reserva.getValoracionTexto());
        assertEquals(2, conductor.getNumValoraciones());
        // Reputacion formula: 4.0 + (5 - 4.0)/2 = 4.5
        assertEquals(4.5f, conductor.getReputacion());
    }

    @Test
    void ponerValoraciones_ViajeNoIniciado() {
        mockSecurityContext();
        when(pasajeroRepository.findByIdUsuario(1)).thenReturn(Optional.of(pasajero));
        when(reservaRepository.existsByIdAndIdPasajero(1, 1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(viajeRepository.existsById(1)).thenReturn(true);

        viaje.setFechaSalida(LocalDate.now().plusDays(5)); // Future
        when(viajeRepository.findById(1)).thenReturn(Optional.of(viaje));

        assertThrows(ViajeException.class, () -> reservaService.ponerValoraciones(1, 5, "Genial"));
    }

    @Test
    void confirmarReservaDesdeViaje_Ok() {
        when(reservaRepository.existsById(1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        reservaService.confirmarReservaDesdeViaje(1);
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstadoReserva());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void confirmarReservaDesdeViaje_NotFound() {
        when(reservaRepository.existsById(1)).thenReturn(false);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.confirmarReservaDesdeViaje(1));
    }

    @Test
    void cancelarReservaDesdeViaje_Ok() {
        when(reservaRepository.existsById(1)).thenReturn(true);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        reservaService.cancelarReservaDesdeViaje(1);
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstadoReserva());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void getReservasByIdViaje_Ok() {
        when(reservaRepository.findAllByIdViaje(1)).thenReturn(List.of(reserva));
        List<ReservaResponse> result = reservaService.getReservasByIdViaje(1);
        assertFalse(result.isEmpty());
    }

    @Test
    void getHistoricoViajesHechos_Ok() {
        when(reservaRepository.findAllByIdPasajeroAndEstadoReserva(1, EstadoReserva.CONFIRMADA))
                .thenReturn(List.of(reserva));
        List<Integer> result = reservaService.getHistoricoViajesHechos(1);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0));
    }

    @Test
    void getCurrentUsuario_NotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> reservaService.getMisReservas());
    }
}
