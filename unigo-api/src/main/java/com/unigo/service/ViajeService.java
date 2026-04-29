package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.ViajeException;
import com.unigo.service.exceptions.ViajeNotFoundException;
import com.unigo.service.mappers.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaService reservaService;

    // ADMIN
    public List<ViajeResponse> findAll() {
        return viajeRepository.findAll().stream()
                .map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    public ViajeResponse findById(int id) {
        if (!viajeRepository.existsById(id)) {
            throw new ViajeNotFoundException("Viaje " + id + " no encontrado");
        }
        return ViajeMapper.mapViajeToDto(viajeRepository.findById(id).get());
    }

    public ViajeResponse createAdmin(int idConductor, ViajeRequest request){
        if (!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        Viaje v = ViajeMapper.mapDtoToViaje(request);
        v.setId(0);
        v.setIdConductor(idConductor);
        // Todos empiezan disponibles
        v.setEstadoViaje(EstadoViaje.DISPONIBLE);
        v.setReservas(new ArrayList<>());
        viajeRepository.save(v);
        return ViajeMapper.mapViajeToDto(v);
    }

    public ViajeResponse updateAdmin(int id, int idConductor, ViajeRequest request){
        if (!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        if (id != request.getId()){
            throw new ViajeException("El id del path y el body no coinciden");
        }
        if (!viajeRepository.existsByIdAndIdConductor(id, idConductor)){
            throw new ViajeNotFoundException("Viaje " + id + " no relacionado con ese conductor");
        }
        Viaje vDB = viajeRepository.findById(id).get();
        vDB.setOrigen(request.getOrigen());
        vDB.setDestino(request.getDestino());
        vDB.setFechaSalida(request.getFechaSalida());
        vDB.setPlazasDisponibles(request.getPlazasDisponibles());
        vDB.setPrecioPlaza(request.getPrecioPorPlaza());
        // NO SE PUEDE CAMBIAR EL ESTADO DESDE AQUÍ
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(vDB);
    }

    public String deleteAdmin(int id){
        if (!viajeRepository.existsById(id)){
            throw new ViajeNotFoundException("Viaje no encontrado");
        }
        this.viajeRepository.deleteById(id);
        return "Viaje " + id + " eliminado con exito";
    }

    public List<ViajeResponse> searchByEstado(String estado){
        estado = estado.trim().toUpperCase();
        EstadoViaje e;
        try {
            e = EstadoViaje.valueOf(estado);
        }catch (IllegalArgumentException ex){
            throw new ViajeException("El string enviado no coincide con ningún estado posible");
        }
        return viajeRepository.findByEstadoViaje(e).stream()
                .map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    public ViajeResponse cambiarEstadoAdmin(int id, String estado){
        if (!viajeRepository.existsById(id)){
            throw new ViajeNotFoundException("Viaje no encontrado");
        }
        estado = estado.trim().toUpperCase();
        EstadoViaje e;
        try {
            e = EstadoViaje.valueOf(estado);
        }catch (IllegalArgumentException ex){
            throw new ViajeException("El string enviado no coincide con ningún estado posible");
        }
        Viaje vDB = viajeRepository.findById(id).get();
        vDB.setEstadoViaje(e);
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(vDB);
    }

    // USER, CONDUCTOR CRUDS
    public List<ViajeResponse> getMisViajes(){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        return this.viajeRepository.findAllByIdConductor(c.get().getId()).stream()
                .map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    public ViajeResponse getMiViajeById(int idViaje){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        Optional<Viaje> v = viajeRepository.findByIdAndIdConductor(idViaje, c.get().getId());
        if(v.isEmpty()){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + idViaje);
        }
        return ViajeMapper.mapViajeToDto(v.get());
    }

    public ViajeResponse createViaje(ViajeRequest request){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        Viaje newViaje = ViajeMapper.mapDtoToViaje(request);
        newViaje.setId(0);
        newViaje.setIdConductor(c.get().getId());
        newViaje.setEstadoViaje(EstadoViaje.DISPONIBLE);
        newViaje.setReservas(new ArrayList<>());
        viajeRepository.save(newViaje);
        return ViajeMapper.mapViajeToDto(newViaje);
    }

    public ViajeResponse updateViaje(int id, ViajeRequest request){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if (id != request.getId()){
            throw new ViajeException("El id del path y el body no coinciden");
        }
        if (!viajeRepository.existsByIdAndIdConductor(id, c.get().getId())){
            throw new ViajeNotFoundException("Viaje " + id + " no relacionado con ese conductor");
        }
        Viaje vDB = viajeRepository.findById(id).get();
        vDB.setOrigen(request.getOrigen());
        vDB.setDestino(request.getDestino());
        vDB.setFechaSalida(request.getFechaSalida());
        vDB.setPlazasDisponibles(request.getPlazasDisponibles());
        vDB.setPrecioPlaza(request.getPrecioPorPlaza());
        // NO SE PUEDE CAMBIAR EL ESTADO DESDE AQUÍ
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(vDB);
    }

    public String deleteViaje(int id){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(!viajeRepository.existsByIdAndIdConductor(id, c.get().getId())){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + id);
        }
        viajeRepository.deleteById(id);
        return "Viaje " + id + " eliminado con exito";
    }

    public ViajeResponse cambiarEstadoUser(int id, String estado){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(!viajeRepository.existsByIdAndIdConductor(id, c.get().getId())){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + id);
        }
        estado = estado.trim().toUpperCase();
        EstadoViaje e;
        try {
            e = EstadoViaje.valueOf(estado);
        }catch (IllegalArgumentException ex){
            throw new ViajeException("El string enviado no coincide con ningún estado posible");
        }
        Viaje vDB = viajeRepository.findById(id).get();
        vDB.setEstadoViaje(e);
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(vDB);
    }

    public ViajeResponse confimarReserva(int idViaje, int idReserva){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(!viajeRepository.existsByIdAndIdConductor(idViaje, c.get().getId())){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + idViaje);
        }
        Viaje vDB = viajeRepository.findById(idViaje).get();
        if(! vDB.getReservas().stream().filter(r -> r.getId() == idReserva && r.getEstadoReserva().equals(EstadoReserva.PENDIENTE)).toList().isEmpty()){
            reservaService.confirmarReservaDesdeViaje(idReserva);
        }else {
            throw new ViajeException("Reserva no existente o ya confirmada");
        }
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(viajeRepository.findById(idViaje).get());
    }

    public ViajeResponse cancelarReserva(int idViaje, int idReserva){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(!viajeRepository.existsByIdAndIdConductor(idViaje, c.get().getId())){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + idViaje);
        }
        Viaje vDB = viajeRepository.findById(idViaje).get();
        if(! vDB.getReservas().stream().filter(r -> r.getId() == idReserva).toList().isEmpty()){
            reservaService.cancelarReservaDesdeViaje(idReserva);
        }else {
            throw new ViajeException("Reserva no existente.");
        }
        viajeRepository.save(vDB);
        return ViajeMapper.mapViajeToDto(viajeRepository.findById(idViaje).get());
    }


    // PASAJERO (tmb USER)
    // todo: filtrado origen y destino
    public List<ViajeResponse> getViajesDisponibles(){
        return viajeRepository.findByEstadoViajeAndFechaSalidaAfter(EstadoViaje.DISPONIBLE, LocalDate.now()).stream()
                .map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    // Interceptor mensajeria
    public boolean puedeAccederAlChat(int idUsuario, int idViaje) {
        Viaje viaje = viajeRepository.findById(idViaje)
                .orElseThrow(() -> new ViajeNotFoundException("Viaje no encontrado"));

        // 1. ¿Es el conductor?
        if (viaje.getConductor().getId() == idUsuario) {
            return true;
        }

        // 2. ¿Es un pasajero con reserva confirmada?
        return viaje.getReservas().stream()
                .anyMatch(reserva ->
                        reserva.getPasajero().getId() == idUsuario &&
                                "CONFIRMADA".equalsIgnoreCase(String.valueOf(reserva.getEstadoReserva()))
                );
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
