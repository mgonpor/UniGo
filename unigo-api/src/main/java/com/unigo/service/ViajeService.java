package com.unigo.service;

import com.unigo.persistence.entities.*;
import com.unigo.persistence.entities.enums.EstadoReserva;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.ReservaRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.VehiculoRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.mappers.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ReservaService reservaService;

    @Value("${precioMaximo:20}")
    private int precioMaximo;

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
        Optional<Viaje> v = viajeRepository.findById(idViaje);
        if(v.isEmpty()){
            throw new ViajeNotFoundException("No hemos encontrado el viaje con id " + idViaje);
        }
        return ViajeMapper.mapViajeToDto(v.get());
    }

    public ViajeResponse createViaje(ViajeRequest request){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(request.getFechaSalida().isBefore(LocalDate.now())){
            throw new ViajeException("La fecha de salida no puede ser anterior a hoy.");
        }
        if (request.getPrecioPorPlaza() > precioMaximo ||  request.getPrecioPorPlaza() < 0){
            throw new ViajeException("Precio no válido");
        }
        if (1 > request.getPlazasDisponibles()){
            throw new ViajeException("Plazas insuficientes");
        }
        if (request.getIdVehiculo() != null
                && !vehiculoRepository.existsByIdAndIdConductor(request.getIdVehiculo(), c.get().getId())) {
            throw new ViajeException("El vehículo no pertenece a este conductor");
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
        if (request.getIdVehiculo() != null
                && !vehiculoRepository.existsByIdAndIdConductor(request.getIdVehiculo(), c.get().getId())) {
            throw new ViajeException("El vehículo no pertenece a este conductor");
        }
        Viaje vDB = viajeRepository.findById(id).get();
        vDB.setOrigen(request.getOrigen());
        vDB.setDestino(request.getDestino());
        vDB.setFechaSalida(request.getFechaSalida());
        vDB.setPlazasDisponibles(request.getPlazasDisponibles());
        vDB.setPrecioPlaza(request.getPrecioPorPlaza());
        vDB.setIdVehiculo(request.getIdVehiculo());
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

    public ViajeResponse confirmarReserva(int idViaje, int idReserva){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if(!viajeRepository.existsByIdAndIdConductor(idViaje, c.get().getId())){
            throw new ViajeNotFoundException("No hemos encontrado tu viaje con id " + idViaje);
        }
        Viaje vDB = viajeRepository.findById(idViaje).get();
        if (vDB.getEstadoViaje() != EstadoViaje.DISPONIBLE) {
            throw new ViajeException("El viaje ya no acepta cambios de reserva (no esta DISPONIBLE)");
        }
        boolean reservaConfirmable = vDB.getReservas().stream()
                .anyMatch(r -> r.getId() == idReserva && r.getEstadoReserva() == EstadoReserva.PENDIENTE);
        if (!reservaConfirmable) {
            throw new ViajeException("Reserva no existente o ya confirmada");
        }
        if (vDB.getPlazasDisponibles() < 1) {
            throw new ViajeException("No hay plazas disponibles para confirmar");
        }
        // Decrementamos la plaza UNA sola vez en este punto (la creacion de
        // la reserva no toca las plazas; solo lo hace la confirmacion).
        vDB.setPlazasDisponibles(vDB.getPlazasDisponibles() - 1);
        viajeRepository.save(vDB);
        reservaService.confirmarReservaDesdeViaje(idReserva);
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

    // CONDUCTOR
    public List<ReservaResponse> getReservasByIdViaje(int idViaje){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()){
            throw new ConductorNotFoundException("No eres conductor.");
        }
        return reservaService.getReservasByIdViaje(idViaje);
    }

    // PASAJERO (tmb USER)
    public ViajeResponse getViajeByIdPasajero(int idViaje){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        Optional<Viaje> vDB = viajeRepository.findByIdAndEstadoViajeAndFechaSalidaAfter(idViaje, EstadoViaje.DISPONIBLE, LocalDate.now());
        if (vDB.isEmpty()){
            throw new ViajeException("Viaje no disponible");
        }
        return ViajeMapper.mapViajeToDto(vDB.get());
    }

    public List<ViajeResponse> getMisViajesPasajero(){
        Optional<Pasajero> p = pasajeroRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (p.isEmpty()){
            throw new PasajeroNotFoundException("No eres pasajero.");
        }
        return viajeRepository.findAllByIdIn(reservaService.getHistoricoViajesHechos(p.get().getId()))
                .stream().map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    // todo: filtrado origen y destino
    public List<ViajeResponse> getViajesDisponibles(){
        // Solo mostramos viajes DISPONIBLES, futuros y con al menos una plaza libre.
        return viajeRepository.findAllByEstadoViajeAndFechaSalidaAfter(EstadoViaje.DISPONIBLE, LocalDate.now()).stream()
                .filter(v -> v.getPlazasDisponibles() > 0)
                .map(ViajeMapper::mapViajeToDto)
                .toList();
    }

    // Interceptor mensajeria. Recibe el id de Usuario (sub del JWT) y comprueba
    // si tiene derecho al chat del viaje (conductor o pasajero con reserva
    // CONFIRMADA). Usa solo queries directas — sin navegar relaciones lazy,
    // porque este metodo se llama desde el interceptor STOMP fuera de contexto HTTP.
    public boolean puedeAccederAlChat(int idUsuario, int idViaje) {
        if (!viajeRepository.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje no encontrado");
        }

        // 1. ¿Es el conductor? Buscamos el Conductor por idUsuario y comparamos con Viaje.idConductor.
        Optional<Conductor> conductor = conductorRepository.findByIdUsuario(idUsuario);
        if (conductor.isPresent() && viajeRepository.existsByIdAndIdConductor(idViaje, conductor.get().getId())) {
            // Viaje.idConductor es el id de entidad del Conductor, no del Usuario.
            return true;
        }

        // 2. ¿Es un pasajero con reserva CONFIRMADA?
        Optional<Pasajero> pasajero = pasajeroRepository.findByIdUsuario(idUsuario);
        if (pasajero.isEmpty()) {
            return false;
        }
        return reservaRepository.existsByIdViajeAndIdPasajeroAndEstadoReserva(
                idViaje, pasajero.get().getId(), EstadoReserva.CONFIRMADA);
    }

    // AUX
    private Usuario getCurrentUsuario(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> u = usuarioRepository.findByUsername(username);
        if (u.isEmpty()){
            throw new UsuarioNotFoundException("No eres usuario.");
        }
        return u.get();
    }
}
