package com.unigo.service;

import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.ReservaRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ReservaRequest;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    // NO UPDATE
    public String deleteReserva(int id){
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
        this.reservaRepository.deleteById(id);  // todo: ver como afecta a la lista en Viaje
        return "Reserva " + id + " eliminada con éxito.";
    }

    // todo: Poner Valoraciones (post viaje)

    // Aux ViajeService
    public void confirmarReservaDesdeViaje(int id){
        if (!reservaRepository.existsById(id)){
            throw new ReservaNotFoundException("La reserva no existe en base de datos, pero si en viaje. INTEGRATION ERROR");
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
