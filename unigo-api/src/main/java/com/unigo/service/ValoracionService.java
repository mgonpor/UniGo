package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.ReservaRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ValoracionResponse;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Agregado de "valoraciones" a partir de las Reservas con valoracionNumerica > 0.
// No hay entidad propia; cada reserva valorada cuenta como una valoracion.
@Service
public class ValoracionService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Valoraciones recibidas por el usuario actual en su rol de conductor.
    // = reservas valoradas de viajes en los que fue conductor.
    public List<ValoracionResponse> getRecibidas() {
        Usuario u = getCurrentUsuario();
        Optional<Conductor> c = conductorRepository.findByIdUsuario(u.getId());
        if (c.isEmpty()) {
            return List.of();
        }
        List<Integer> idsViajes = viajeRepository.findAllByIdConductor(c.get().getId())
                .stream().map(Viaje::getId).toList();
        if (idsViajes.isEmpty()) return List.of();

        return reservaRepository.findAll().stream()
                .filter(r -> idsViajes.contains(r.getIdViaje()))
                .filter(r -> r.getValoracionNumerica() > 0)
                .map(this::toRecibida)
                .toList();
    }

    // Valoraciones dadas por el usuario actual en su rol de pasajero.
    // = sus propias reservas con valoracionNumerica > 0.
    public List<ValoracionResponse> getDadas() {
        Usuario u = getCurrentUsuario();
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(u.getId());
        if (p.isEmpty()) {
            return List.of();
        }
        return reservaRepository.findAllByIdPasajero(p.get().getId()).stream()
                .filter(r -> r.getValoracionNumerica() > 0)
                .map(this::toDada)
                .toList();
    }

    private ValoracionResponse toRecibida(Reserva r) {
        Viaje v = viajeRepository.findById(r.getIdViaje()).orElse(null);
        Pasajero pas = r.getPasajero();
        Usuario otro = pas != null ? pas.getUsuario() : null;
        return ValoracionResponse.builder()
                .idReserva(r.getId())
                .idViaje(r.getIdViaje())
                .puntuacion(r.getValoracionNumerica())
                .comentario(r.getValoracionTexto())
                .fecha(v != null ? v.getFechaSalida() : r.getFechaReserva())
                .origen(v != null ? v.getOrigen() : null)
                .destino(v != null ? v.getDestino() : null)
                .otroIdUsuario(otro != null ? otro.getId() : null)
                .otroNombre(otro != null ? otro.getNombre() : null)
                .otroUsername(otro != null ? otro.getUsername() : null)
                .build();
    }

    private ValoracionResponse toDada(Reserva r) {
        Viaje v = viajeRepository.findById(r.getIdViaje()).orElse(null);
        Usuario otro = null;
        if (v != null && v.getConductor() != null) {
            otro = v.getConductor().getUsuario();
        }
        return ValoracionResponse.builder()
                .idReserva(r.getId())
                .idViaje(r.getIdViaje())
                .puntuacion(r.getValoracionNumerica())
                .comentario(r.getValoracionTexto())
                .fecha(v != null ? v.getFechaSalida() : r.getFechaReserva())
                .origen(v != null ? v.getOrigen() : null)
                .destino(v != null ? v.getDestino() : null)
                .otroIdUsuario(otro != null ? otro.getId() : null)
                .otroNombre(otro != null ? otro.getNombre() : null)
                .otroUsername(otro != null ? otro.getUsername() : null)
                .build();
    }

    private Usuario getCurrentUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("No eres usuario."));
    }
}
