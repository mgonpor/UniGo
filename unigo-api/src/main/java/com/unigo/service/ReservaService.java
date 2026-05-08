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

    // ADMIN
    public List<ReservaResponse> findAll(){
        return reservaRepository.findAll().stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse findById(int id){
        if(!reservaRepository.existsById(id)){
            throw new ReservaNotFoundException("Reserva con id " + id + " no encontrada");
        }
        return ReservaMapper.mapReservaToDto(reservaRepository.findById(id).get());
    }

    public ReservaResponse createAdmin(int idPasajero, int idViaje){
        if(!pasajeroRepository.existsById(idPasajero)){
            throw new PasajeroNotFoundException("Pasajero " + idPasajero + " no encontrado");
        }
        if(!viajeRepository.existsById(idViaje)){
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }else if(viajeRepository.findById(idViaje).get().getEstadoViaje() != EstadoViaje.DISPONIBLE){
            throw new ViajeException("Viaje no disponible");
        }
        Viaje v = viajeRepository.findById(idViaje).get();
        if(v.getPlazasDisponibles() < 1){
            throw new ViajeException("No hay plazas disponibles");
        }else{
            v.setPlazasDisponibles(v.getPlazasDisponibles()-1);
            viajeRepository.save(v);
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

    public ReservaResponse updateAdmin(int idReserva, int idPasajero, int idViaje, ReservaRequest request){
        if(idReserva != request.getId()){
            throw new ReservaException("El id del path y el body de la reserva no coinciden");
        }
        if(idPasajero != request.getIdPasajero()){
            throw new ReservaException("El id del path y el body de pasajero no coinciden");
        }
        if(!reservaRepository.existsById(idReserva)){
            throw new ReservaNotFoundException("La reserva indicada no existe");
        }
        if(!pasajeroRepository.existsById(idPasajero)){
            throw new PasajeroNotFoundException("Pasajero " + idPasajero + " no encontrado");
        }
        if(!viajeRepository.existsById(idViaje)){
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }else if(viajeRepository.findById(idViaje).get().getEstadoViaje() != EstadoViaje.DISPONIBLE){
            throw new ViajeException("Viaje no disponible");
        }
        EstadoReserva e;
        try {
            e = EstadoReserva.valueOf(request.getEstadoReserva().trim().toUpperCase());
        }catch (IllegalArgumentException ex){
            throw new ReservaException("El string enviado no coincide con ningún estado posible");
        }
        Reserva r = ReservaMapper.mapDtoToReserva(request);
        r.setIdViaje(idViaje);
        r.setEstadoReserva(e);
        reservaRepository.save(r);
        return ReservaMapper.mapReservaToDto(r);
    }

    public String deleteAdmin(int id) {
        if(!reservaRepository.existsById(id)){
            throw new ReservaNotFoundException("Reserva con id " + id + " no encontrada");
        }
        Reserva r = reservaRepository.findById(id).get();
        Viaje v = viajeRepository.findById(r.getIdViaje()).get();
        v.setPlazasDisponibles(v.getPlazasDisponibles()+1);
        reservaRepository.deleteById(id);
        return "Reserva " + id + " eliminada con éxito.";
    }

    // USER
    public List<ReservaResponse> getMisReservas(){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        return reservaRepository.findAllByIdPasajero(p.get().getId()).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse getMiReservaById(int id){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        if(!reservaRepository.existsByIdAndIdPasajero(id, p.get().getId())){
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        return ReservaMapper.mapReservaToDto(reservaRepository.findByIdAndIdPasajero(id, p.get().getId()).get());
    }

    public List<ReservaResponse> getMisReservasByEstado(String estado){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        estado = estado.trim().toUpperCase();
        EstadoReserva e;
        try {
            e = EstadoReserva.valueOf(estado);
        }catch (IllegalArgumentException ex){
            throw new ViajeException("El string enviado no coincide con ningún estado posible");
        }
        return reservaRepository.findAllByIdPasajeroAndEstadoReserva(p.get().getId(), e).stream()
                .map(ReservaMapper::mapReservaToDto)
                .toList();
    }

    public ReservaResponse createReserva(int idViaje){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        if(!viajeRepository.existsById(idViaje)){
            throw new ViajeNotFoundException("Viaje " + idViaje + " no encontrado");
        }else if(viajeRepository.findById(idViaje).get().getEstadoViaje() != EstadoViaje.DISPONIBLE){
            throw new ViajeException("Viaje no disponible");
        }
        Viaje v = viajeRepository.findById(idViaje).get();
        if(v.getPlazasDisponibles() < 1){
            throw new ViajeException("No hay plazas disponibles");
        }else{
            v.setPlazasDisponibles(v.getPlazasDisponibles()-1);
            viajeRepository.save(v);
        }
        return this.createAdmin(p.get().getId(), idViaje);
    }
    // NO UPDATE NI DELETE
    public String candelarReservaPasajero(int id){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        if(!reservaRepository.existsByIdAndIdPasajero(id, p.get().getId())){
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        Reserva r = reservaRepository.findByIdAndIdPasajero(id, p.get().getId()).get();
        Viaje v = viajeRepository.findById(r.getIdViaje()).get();
        v.setPlazasDisponibles(v.getPlazasDisponibles()+1);
        viajeRepository.save(v);
        r.setEstadoReserva(EstadoReserva.CANCELADA);
        this.reservaRepository.deleteById(id);
        return "Reserva " + id + " eliminada con éxito.";
    }

    public ReservaResponse ponerValoraciones(int idViaje, int idReserva, int valNum, String valText){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new ConductorNotFoundException("No eres pasajero.");
        }
        if(!reservaRepository.existsByIdAndIdPasajero(idReserva, p.get().getId())){
            throw new ReservaNotFoundException("Reserva no encontrada");
        }
        if(! reservaRepository.existsByIdAndIdViaje(idReserva, idViaje)){
            throw new ReservaNotFoundException("Reserva y viaje no relacionados");
        }
        if (viajeRepository.findById(idViaje).get().getFechaSalida().isBefore(LocalDate.now())){
            throw new ViajeException("El viaje aún no ha iniciado");
        }
        Reserva rDB = reservaRepository.findByIdAndIdViaje(idReserva, idViaje).get();
        if(0 < valNum && valNum <= 5) {
            // Lógica actualizar Conductor
            Optional<Conductor> optC = conductorRepository.findById(viajeRepository.findById(idViaje).get().getIdConductor());
            if (optC.isEmpty()){
                throw new ConductorNotFoundException("No conductor del viaje no encontrado");
            }
            Conductor cDB = optC.get();
            cDB.setReputacion(
                    (float) (cDB.getReputacion() +  (valNum - cDB.getReputacion()) / (cDB.getNumValoraciones()+1.0) )
            );
            cDB.setNumValoraciones(cDB.getNumValoraciones()+1);
            rDB.setValoracionNumerica(valNum);
            rDB.setValoracionTexto(Objects.requireNonNullElse(valText, ""));
        }
        return ReservaMapper.mapReservaToDto(reservaRepository.save(rDB));
    }

    // Aux ViajeService
    public void confirmarReservaDesdeViaje(int id){
        if (!reservaRepository.existsById(id)){
            throw new ReservaNotFoundException("La reserva no existe en su tabla, pero si en viaje. INTEGRATION ERROR");
        }
        Reserva r = reservaRepository.findById(id).get();
        r.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reservaRepository.save(r);
    }

    public void cancelarReservaDesdeViaje(int id){
        if (!reservaRepository.existsById(id)){
            throw new ReservaNotFoundException("La reserva no existe en base de datos, pero si en viaje. INTEGRATION ERROR");
        }
        Reserva r = reservaRepository.findById(id).get();
        r.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(r);
    }

    public List<Integer> getHistoricoViajesHechos(int idPasajero){
        return reservaRepository.findAllByIdPasajeroAndEstadoReserva(idPasajero, EstadoReserva.CONFIRMADA)
                .stream().map(Reserva::getIdViaje)
                .toList();
    }

    // AUX
    private Usuario getCurrentUsuario(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> u = usuarioRepository.findByUsername(username);
        if (u.isEmpty()){
            throw new ConductorNotFoundException("No eres usuario.");
        }
        return u.get();
    }
}
