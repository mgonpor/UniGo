package com.unigo.service;

import com.unigo.persistence.entities.*;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.*;
import com.unigo.service.dtos.ReservaRequest;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    // ──────────────────────────────────────────────────────────────────
    //  Reglas de plazas
    //  - Al crear reserva (PENDIENTE) NO se tocan las plazas.
    //  - Al CONFIRMAR (conductor) se decrementan plazas en uno.
    //  - Al CANCELAR (pasajero/conductor/admin) se re-incrementan SOLO si
    //    la reserva estaba CONFIRMADA.
    //  - Las reservas no se borran del historico al cancelar; se marcan
    //    como CANCELADA. Solo el admin puede borrar fisicamente.
    // ──────────────────────────────────────────────────────────────────

    // ADMIN
    public List<ReservaResponse> findAll() {
        return reservaRepository.findAll().stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse findById(int id) {
        if (!reservaRepository.existsById(id)) {
            throw new ReservaNotFoundException("Reserva con id " + id + " no encontrada");
        }
        return ReservaMapper.mapReservaToDto(reservaRepository.findById(id).get());
    }

    public ReservaResponse createAdmin(int idPasajero, int idViaje) {
        if (!pasajeroRepository.existsById(idPasajero)) {
            throw new PasajeroNotFoundException("Pasajero " + idPasajero + " no encontrado");
        }
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }
        Viaje v = viajeRepository.findById(idViaje).get();
        if (v.getEstadoViaje() != EstadoViaje.DISPONIBLE) {
            throw new ViajeException("Viaje no disponible");
        }
        if (v.getPlazasDisponibles() < 1) {
            throw new ViajeException("No hay plazas disponibles");
        }
        // No permitimos reservar dos veces el mismo viaje mientras siga activa
        boolean yaReservado = reservaRepository.findAllByIdPasajero(idPasajero).stream()
                .anyMatch(r -> r.getIdViaje() == idViaje
                        && (r.getEstadoReserva() == EstadoReserva.PENDIENTE
                                || r.getEstadoReserva() == EstadoReserva.CONFIRMADA));
        if (yaReservado) {
            throw new ReservaException("Ya tienes una reserva activa para este viaje");
        }

        Reserva r = new Reserva();
        r.setId(0);
        r.setIdViaje(idViaje);
        r.setIdPasajero(idPasajero);
        r.setFechaReserva(LocalDate.now());
        r.setEstadoReserva(EstadoReserva.PENDIENTE);
        r.setValoracionNumerica(-1);
        r.setValoracionTexto("");
        reservaRepository.save(r);
        return ReservaMapper.mapReservaToDto(r);
    }

    public ReservaResponse updateAdmin(int idReserva, int idPasajero, int idViaje, ReservaRequest request) {
        if (idReserva != request.getId()) {
            throw new ReservaException("El id del path y el body de la reserva no coinciden");
        }
        if (idPasajero != request.getIdPasajero()) {
            throw new ReservaException("El id del path y el body de pasajero no coinciden");
        }
        if (!reservaRepository.existsById(idReserva)) {
            throw new ReservaNotFoundException("La reserva indicada no existe");
        }
        if (!pasajeroRepository.existsById(idPasajero)) {
            throw new PasajeroNotFoundException("Pasajero " + idPasajero + " no encontrado");
        }
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }
        if (viajeRepository.findById(idViaje).get().getEstadoViaje() != EstadoViaje.DISPONIBLE) {
            throw new ViajeException("Viaje no disponible");
        }
        EstadoReserva e;
        try {
            e = EstadoReserva.valueOf(request.getEstadoReserva().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ReservaException("El string enviado no coincide con ningun estado posible");
        }
        Reserva r = ReservaMapper.mapDtoToReserva(request);
        r.setIdViaje(idViaje);
        r.setEstadoReserva(e);
        r.setValoracionNumerica(request.getValoracionNumerica());
        r.setValoracionTexto(request.getValoracionTexto());
        reservaRepository.save(r);
        return ReservaMapper.mapReservaToDto(r);
    }

    public String deleteAdmin(int id) {
        if (!reservaRepository.existsById(id)) {
            throw new ReservaNotFoundException("Reserva con id " + id + " no encontrada");
        }
        Reserva r = reservaRepository.findById(id).get();
        // Solo devolvemos la plaza si la reserva consumia una (estaba CONFIRMADA).
        if (r.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            viajeRepository.findById(r.getIdViaje()).ifPresent(v -> {
                v.setPlazasDisponibles(v.getPlazasDisponibles() + 1);
                viajeRepository.save(v);
            });
        }
        reservaRepository.deleteById(id);
        return "Reserva " + id + " eliminada con exito.";
    }

    // USER PASAJERO
    public List<ReservaResponse> getMisReservas() {
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        return reservaRepository.findAllByIdPasajero(p.get().getId()).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse getMiReservaById(int id) {
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        if (!reservaRepository.existsByIdAndIdPasajero(id, p.get().getId())) {
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        return ReservaMapper.mapReservaToDto(reservaRepository.findByIdAndIdPasajero(id, p.get().getId()).get());
    }

    public List<ReservaResponse> getMisReservasByEstado(String estado) {
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        estado = estado.trim().toUpperCase();
        EstadoReserva e;
        try {
            e = EstadoReserva.valueOf(estado);
        } catch (IllegalArgumentException ex) {
            throw new ReservaException("El string enviado no coincide con ningun estado posible");
        }
        return reservaRepository.findAllByIdPasajeroAndEstadoReserva(p.get().getId(), e).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse createReserva(int idViaje) {
        Usuario u = getCurrentUsuario();
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(u.getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }
        Viaje v = viajeRepository.findById(idViaje).get();

        // Regla: no puedes reservar tu propio viaje
        Optional<Conductor> conductor = conductorRepository.findByIdUsuario(u.getId());
        if (conductor.isPresent() && conductor.get().getId() == v.getIdConductor()) {
            throw new ReservaException("No puedes reservar plaza en tu propio viaje");
        }

        return this.createAdmin(p.get().getId(), idViaje);
    }

    // El pasajero cancela su propia reserva. No borramos: marcamos CANCELADA
    // para conservar el historial. Solo devolvemos plaza si estaba CONFIRMADA.
    public String cancelarReservaPasajero(int id) {
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        if (!reservaRepository.existsByIdAndIdPasajero(id, p.get().getId())) {
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        Reserva r = reservaRepository.findByIdAndIdPasajero(id, p.get().getId()).get();
        if (r.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new ReservaException("La reserva ya estaba cancelada");
        }
        if (r.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            Viaje v = viajeRepository.findById(r.getIdViaje())
                    .orElseThrow(() -> new ViajeNotFoundException("Viaje " + r.getIdViaje() + " no encontrado"));
            v.setPlazasDisponibles(v.getPlazasDisponibles() + 1);
            viajeRepository.save(v);
        }
        r.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(r);
        return "Reserva " + id + " cancelada con exito.";
    }

    // Solo se permite valorar reservas CONFIRMADAS de viajes cuya fecha ya
    // ha pasado (o cuyo estado del viaje es COMPLETADO) y solo una vez.
    public ReservaResponse ponerValoraciones(int idReserva, int valNum, String valText) {
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        if (!reservaRepository.existsByIdAndIdPasajero(idReserva, p.get().getId())) {
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        Reserva rDB = reservaRepository.findById(idReserva).get();
        if (rDB.getEstadoReserva() != EstadoReserva.CONFIRMADA) {
            throw new ReservaException("Solo puedes valorar viajes que has hecho (reservas confirmadas).");
        }
        Viaje v = viajeRepository.findById(rDB.getIdViaje())
                .orElseThrow(() -> new ViajeNotFoundException("Viaje " + rDB.getIdViaje() + " no encontrado"));
        boolean viajeRealizado = v.getEstadoViaje() == EstadoViaje.COMPLETADO
                || !v.getFechaSalida().isAfter(LocalDate.now());
        if (!viajeRealizado) {
            throw new ViajeException("Aun no puedes valorar; el viaje todavia no se ha realizado.");
        }
        if (rDB.getValoracionNumerica() > 0) {
            throw new ReservaException("Ya has valorado este viaje");
        }
        if (valNum < 1 || valNum > 5) {
            throw new ReservaException("La valoracion debe estar entre 1 y 5.");
        }
        Conductor cDB = conductorRepository.findById(v.getIdConductor())
                .orElseThrow(() -> new ConductorNotFoundException("Conductor del viaje no encontrado"));
        // Media incremental tratando "reputacion = -1" (sin valoraciones) como cero base.
        double prev = cDB.getReputacion() < 0 ? 0.0 : cDB.getReputacion();
        int n = cDB.getNumValoraciones();
        cDB.setReputacion((float) ((prev * n + valNum) / (n + 1.0)));
        cDB.setNumValoraciones(n + 1);
        conductorRepository.save(cDB);

        rDB.setValoracionNumerica(valNum);
        rDB.setValoracionTexto(Objects.requireNonNullElse(valText, ""));
        return ReservaMapper.mapReservaToDto(reservaRepository.save(rDB));
    }

    // ─── Llamados desde ViajeService (confirmar/cancelar por conductor) ──

    // Marca CONFIRMADA. El control de plazas y de identidad del conductor lo
    // hace ViajeService antes de invocar este metodo.
    public void confirmarReservaDesdeViaje(int id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException(
                        "La reserva no existe en su tabla, pero si en viaje. INTEGRATION ERROR"));
        r.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reservaRepository.save(r);
    }

    // Marca CANCELADA y devuelve la plaza al viaje SOLO si la reserva estaba
    // CONFIRMADA (los PENDIENTES no consumen plaza).
    public void cancelarReservaDesdeViaje(int id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException(
                        "La reserva no existe en base de datos, pero si en viaje. INTEGRATION ERROR"));
        if (r.getEstadoReserva() == EstadoReserva.CANCELADA) {
            return; // idempotente
        }
        if (r.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            viajeRepository.findById(r.getIdViaje()).ifPresent(v -> {
                v.setPlazasDisponibles(v.getPlazasDisponibles() + 1);
                viajeRepository.save(v);
            });
        }
        r.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(r);
    }

    public List<ReservaResponse> getReservasByIdViaje(int idViaje) {
        return reservaRepository.findAllByIdViaje(idViaje).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public List<Integer> getHistoricoViajesHechos(int idPasajero) {
        return reservaRepository.findAllByIdPasajeroAndEstadoReserva(idPasajero, EstadoReserva.CONFIRMADA)
                .stream().map(Reserva::getIdViaje)
                .toList();
    }

    public List<ReservaResponse> getReservasByViaje(int idViaje) {
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }
        return reservaRepository.findAllByIdViaje(idViaje).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public List<ReservaResponse> getReservasConductor() {
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()) {
            throw new ConductorNotFoundException("Aun no eres conductor.");
        }
        List<Viaje> misViajes = viajeRepository.findAllByIdConductor(c.get().getId());
        List<Integer> idsViajes = misViajes.stream().map(Viaje::getId).toList();
        return reservaRepository.findAll().stream()
                .filter(r -> idsViajes.contains(r.getIdViaje()))
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    // Endpoint generico de cambio de estado. Requiere que el usuario sea el
    // conductor del viaje de la reserva. Aplica las invariantes de plazas
    // segun la transicion.
    public ReservaResponse cambiarEstadoReserva(int idReserva, String estado) {
        if (!reservaRepository.existsById(idReserva)) {
            throw new ReservaNotFoundException("Reserva con id " + idReserva + " no encontrada");
        }
        Reserva r = reservaRepository.findById(idReserva).get();
        Viaje v = viajeRepository.findById(r.getIdViaje())
                .orElseThrow(() -> new ViajeNotFoundException("Viaje " + r.getIdViaje() + " no encontrado"));
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty() || c.get().getId() != v.getIdConductor()) {
            throw new ReservaException("Solo el conductor del viaje puede cambiar el estado de la reserva");
        }
        estado = estado.trim().toUpperCase();
        EstadoReserva nuevo;
        try {
            nuevo = EstadoReserva.valueOf(estado);
        } catch (IllegalArgumentException ex) {
            throw new ReservaException("El string enviado no coincide con ningun estado posible");
        }
        EstadoReserva anterior = r.getEstadoReserva();
        if (anterior == nuevo) {
            return ReservaMapper.mapReservaToDto(r); // no-op
        }
        // Transiciones que afectan a las plazas
        if (anterior != EstadoReserva.CONFIRMADA && nuevo == EstadoReserva.CONFIRMADA) {
            if (v.getPlazasDisponibles() < 1) {
                throw new ReservaException("No hay plazas disponibles para confirmar");
            }
            v.setPlazasDisponibles(v.getPlazasDisponibles() - 1);
            viajeRepository.save(v);
        } else if (anterior == EstadoReserva.CONFIRMADA && nuevo != EstadoReserva.CONFIRMADA) {
            v.setPlazasDisponibles(v.getPlazasDisponibles() + 1);
            viajeRepository.save(v);
        }
        r.setEstadoReserva(nuevo);
        reservaRepository.save(r);
        return ReservaMapper.mapReservaToDto(r);
    }

    // AUX
    private Usuario getCurrentUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> u = usuarioRepository.findByUsername(username);
        if (u.isEmpty()) {
            throw new UsuarioNotFoundException("No eres usuario.");
        }
        return u.get();
    }
}
