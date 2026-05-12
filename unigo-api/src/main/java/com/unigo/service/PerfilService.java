package com.unigo.service;

import com.unigo.persistence.entities.*;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.*;
import com.unigo.service.dtos.PerfilResponse;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.dtos.ViajeResponse;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import com.unigo.service.mappers.ReservaMapper;
import com.unigo.service.mappers.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ConductorRepository conductorRepository;
    @Autowired private PasajeroRepository pasajeroRepository;
    @Autowired private ViajeRepository viajeRepository;
    @Autowired private ReservaRepository reservaRepository;

    public PerfilResponse getMiPerfil() {
        Usuario u = getCurrentUsuario();

        Optional<Conductor> optConductor = conductorRepository.findByIdUsuario(u.getId());
        Optional<Pasajero>  optPasajero  = pasajeroRepository.findByIdUsuario(u.getId());

        // ── Stats como conductor ──────────────────────────────────────────
        int totalViajes   = 0;
        float reputacion  = -1f;
        int numValRec     = 0;
        List<ViajeResponse> ultimosViajes = List.of();

        if (optConductor.isPresent()) {
            Conductor c = optConductor.get();
            List<Viaje> misViajes = viajeRepository.findAllByIdConductor(c.getId());
            totalViajes   = misViajes.size();
            reputacion    = c.getReputacion();
            numValRec     = c.getNumValoraciones();
            ultimosViajes = misViajes.stream()
                    .sorted(Comparator.comparing(Viaje::getFechaSalida).reversed())
                    .limit(5)
                    .map(ViajeMapper::mapViajeToDto)
                    .toList();
        }

        // ── Stats como pasajero ───────────────────────────────────────────
        int totalComoPas   = 0;
        int totalRealizados = 0;
        int numValDadas    = 0;
        List<ReservaResponse> ultimasReservas = List.of();

        if (optPasajero.isPresent()) {
            Pasajero p = optPasajero.get();
            List<Reserva> misReservas = reservaRepository.findAllByIdPasajero(p.getId());

            List<Reserva> confirmadas = misReservas.stream()
                    .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
                    .toList();
            totalComoPas = confirmadas.size();

            totalRealizados = (int) confirmadas.stream()
                    .filter(r -> {
                        Viaje v = viajeRepository.findById(r.getIdViaje()).orElse(null);
                        if (v == null) return false;
                        return v.getEstadoViaje() == EstadoViaje.COMPLETADO
                                || !v.getFechaSalida().isAfter(LocalDate.now());
                    })
                    .count();

            numValDadas = (int) misReservas.stream()
                    .filter(r -> r.getValoracionNumerica() > 0)
                    .count();

            ultimasReservas = misReservas.stream()
                    .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA)
                    .sorted(Comparator.comparing(Reserva::getFechaReserva).reversed())
                    .limit(5)
                    .map(ReservaMapper::mapReservaToDto)
                    .toList();
        }

        return PerfilResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .username(u.getUsername())
                .email(u.getEmail())
                .esConductor(optConductor.isPresent())
                .esPasajero(optPasajero.isPresent())
                .totalViajesComoPasajero(totalComoPas)
                .totalViajesRealizados(totalRealizados)
                .totalViajesComoConductor(totalViajes)
                .reputacion(reputacion)
                .numValoracionesRecibidas(numValRec)
                .numValoracionesDadas(numValDadas)
                .conductor(optConductor.map(ConductorMapper::mapConductorToDto).orElse(null))
                .ultimasReservas(ultimasReservas)
                .ultimosViajes(ultimosViajes)
                .build();
    }

    private Usuario getCurrentUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("No eres usuario."));
    }
}
