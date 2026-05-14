package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Mensaje;
import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.MensajeRepository;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.ReservaRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ChatResumenResponse;
import com.unigo.service.dtos.MensajeRequest;
import com.unigo.service.dtos.MensajeResponse;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.exceptions.ViajeNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ViajeService viajeService;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Persiste un mensaje entrante por WebSocket. La autorizacion la hace el caller.
    public MensajeResponse guardarMensaje(MensajeRequest request) {
        Mensaje nuevoMensaje = new Mensaje();
        nuevoMensaje.setIdRemitente(request.getIdRemitente());
        nuevoMensaje.setIdViaje(request.getIdViaje());
        nuevoMensaje.setTexto(request.getTexto());
        nuevoMensaje.setFechaEnvio(LocalDateTime.now());
        Mensaje mensajeGuardado = mensajeRepository.save(nuevoMensaje);
        return mapToResponse(mensajeGuardado);
    }

    // Historial de un chat de viaje, en orden cronologico. Solo si el usuario actual
    // tiene derecho (conductor o pasajero con reserva confirmada).
    public List<MensajeResponse> getHistorial(int idViaje) {
        Usuario u = getCurrentUsuario();
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje no encontrado");
        }
        if (!viajeService.puedeAccederAlChat(u.getId(), idViaje)) {
            throw new AccessDeniedException("No tienes acceso al chat de este viaje");
        }
        return mensajeRepository.findByIdViajeOrderByFechaEnvioAsc(idViaje).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Lista de chats accesibles para el usuario actual: viajes donde es conductor o
    // tiene una reserva CONFIRMADA. Cada entrada incluye un resumen del ultimo mensaje.
    public List<ChatResumenResponse> getMisChats() {
        Usuario u = getCurrentUsuario();

        // 1. Viajes donde soy el conductor
        Optional<Conductor> optConductor = conductorRepository.findByIdUsuario(u.getId());
        List<Viaje> viajesConductor = optConductor
                .map(c -> viajeRepository.findAllByIdConductor(c.getId()))
                .orElse(List.of());

        // 2. Viajes donde soy pasajero con reserva CONFIRMADA
        Optional<Pasajero> optPasajero = pasajeroRepository.findByIdUsuario(u.getId());
        List<Viaje> viajesPasajero = List.of();
        if (optPasajero.isPresent()) {
            List<Integer> idsViaje = reservaRepository
                    .findAllByIdPasajeroAndEstadoReserva(optPasajero.get().getId(), EstadoReserva.CONFIRMADA)
                    .stream()
                    .map(Reserva::getIdViaje)
                    .toList();
            if (!idsViaje.isEmpty()) {
                viajesPasajero = viajeRepository.findAllByIdIn(idsViaje);
            }
        }

        // 3. Union sin duplicados, manteniendo orden estable
        Map<Integer, Viaje> indexado = new LinkedHashMap<>();
        for (Viaje v : viajesConductor) indexado.putIfAbsent(v.getId(), v);
        for (Viaje v : viajesPasajero) indexado.putIfAbsent(v.getId(), v);

        // 4. Construir resumen para cada viaje
        Integer idConductorUsuario = optConductor.map(Conductor::getId).orElse(null);
        List<ChatResumenResponse> resultado = new ArrayList<>();
        for (Viaje v : indexado.values()) {
            resultado.add(buildResumen(v, idConductorUsuario));
        }

        // 5. Ordenar por fecha de ultimo mensaje (mas reciente primero); los chats sin mensajes al final.
        resultado.sort((a, b) -> {
            LocalDateTime fa = a.getUltimoMensajeFecha();
            LocalDateTime fb = b.getUltimoMensajeFecha();
            if (fa == null && fb == null) return 0;
            if (fa == null) return 1;
            if (fb == null) return -1;
            return fb.compareTo(fa);
        });
        return resultado;
    }

    private ChatResumenResponse buildResumen(Viaje v, Integer idConductorUsuario) {
        Optional<Mensaje> ultimo = mensajeRepository.findFirstByIdViajeOrderByFechaEnvioDesc(v.getId());

        int participantes = 1; // el conductor
        if (v.getReservas() != null) {
            participantes += (int) v.getReservas().stream()
                    .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
                    .count();
        }

        return ChatResumenResponse.builder()
                .idViaje(v.getId())
                .origen(v.getOrigen())
                .destino(v.getDestino())
                .fechaSalida(v.getFechaSalida())
                .estadoViaje(v.getEstadoViaje() != null ? v.getEstadoViaje().toString() : null)
                .conductor(v.getConductor() != null ? ConductorMapper.mapConductorToDto(v.getConductor()) : null)
                .participantesCount(participantes)
                .ultimoMensajeTexto(ultimo.map(Mensaje::getTexto).orElse(null))
                .ultimoMensajeFecha(ultimo.map(Mensaje::getFechaEnvio).orElse(null))
                .ultimoMensajeIdRemitente(ultimo.map(Mensaje::getIdRemitente).orElse(null))
                .soyConductor(idConductorUsuario != null && v.getIdConductor() == idConductorUsuario)
                .build();
    }

    private MensajeResponse mapToResponse(Mensaje m) {
        return MensajeResponse.builder()
                .id(m.getId())
                .idRemitente(m.getIdRemitente())
                .idViaje(m.getIdViaje())
                .texto(m.getTexto())
                .fechaEnvio(m.getFechaEnvio())
                .build();
    }

    private Usuario getCurrentUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("No eres usuario."));
    }
}
