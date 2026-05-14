package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.entities.Vehiculo;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.persistence.repositories.VehiculoRepository;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.exceptions.VehiculoException;
import com.unigo.service.exceptions.VehiculoNotFoundException;
import com.unigo.service.mappers.VehiculoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConductorService conductorService;

    // ADMIN
    public List<VehiculoResponse> findAll() {
        return vehiculoRepository.findAll().stream()
                .map(VehiculoMapper::mapVehiculoToDto)
                .toList();
    }

    public VehiculoResponse findById(int idVehiculo) {
        if (!vehiculoRepository.existsById(idVehiculo)) {
            throw new VehiculoNotFoundException("Vehiculo no encontrado");
        }
        return VehiculoMapper.mapVehiculoToDto(vehiculoRepository.findById(idVehiculo).get());
    }

    // NO SE CREA AUTOMÁTICAMENTE
    public VehiculoResponse createAdmin(int idConductor, VehiculoRequest vehiculoRequest) {
        if (!conductorRepository.existsById(idConductor)) {
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        Vehiculo vehiculo = VehiculoMapper.mapDtoToVehiculo(vehiculoRequest);
        vehiculo.setIdConductor(idConductor);
        vehiculo.setId(0);
        vehiculoRepository.save(vehiculo);
        return VehiculoMapper.mapVehiculoToDto(vehiculo);
    }

    public VehiculoResponse updateAdmin(int idVehiculo, int idConductor, VehiculoRequest vehiculoRequest) {
        if (!conductorRepository.existsById(idConductor)) {
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        if (idVehiculo != vehiculoRequest.getId()) {
            throw new VehiculoException("El id del path y el body no coinciden.");
        }
        if (!vehiculoRepository.existsByIdAndIdConductor(idVehiculo, idConductor)) {
            throw new VehiculoNotFoundException("Vehiculo no encontrado");
        }
        Vehiculo vDB = this.vehiculoRepository.findById(idVehiculo).get();
        vDB.setMarca(vehiculoRequest.getMarca());
        vDB.setModelo(vehiculoRequest.getModelo());
        vDB.setColor(vehiculoRequest.getColor());
        vDB.setMatricula(vehiculoRequest.getMatricula());
        this.vehiculoRepository.save(vDB);
        return VehiculoMapper.mapVehiculoToDto(vDB);
    }

    public String deleteAdmin(int idVehiculo) {
        if (!vehiculoRepository.existsById(idVehiculo)) {
            throw new VehiculoNotFoundException("Vehiculo no encontrado");
        }
        this.vehiculoRepository.deleteById(idVehiculo);
        return "Vehiculo " + idVehiculo + " eliminado";
    }

    // CRUDs USER
    public List<VehiculoResponse> getMisVehiculos() {
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()) {
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        return vehiculoRepository.findAllByIdConductor(c.get().getId()).stream()
                .map(VehiculoMapper::mapVehiculoToDto)
                .toList();
    }

    // Get by id
    public VehiculoResponse getVehiculoById(int idVehiculo) {
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()) {
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if (!vehiculoRepository.existsByIdAndIdConductor(idVehiculo, c.get().getId())) {
            throw new VehiculoNotFoundException("No tiene un vehículo con dicho id");
        }
        return VehiculoMapper.mapVehiculoToDto(vehiculoRepository.findById(idVehiculo).get());
    }

    public VehiculoResponse createVehiculo(VehiculoRequest request) {
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        int idConductor = c.map(Conductor::getId)
                .orElseGet(() -> conductorService.autoCreate(getCurrentUsuario().getId()).getId());

        Vehiculo v = VehiculoMapper.mapDtoToVehiculo(request);
        String matricula = request.getMatricula().trim().toUpperCase();
        if (!matricula.matches("^[0-9]{4}[B-DF-HJ-NP-TV-Z]{3}$")) {
            throw new VehiculoException("La matrícula debe ser válida.");
        }
        v.setId(0);
        v.setIdConductor(idConductor);
        v.setMatricula(matricula);

        return VehiculoMapper.mapVehiculoToDto(vehiculoRepository.save(v));
    }

    public VehiculoResponse updateVehiculo(int idVehiculo, VehiculoRequest request) {
        if (idVehiculo != request.getId()) {
            throw new VehiculoException("El id del path y el body no coinciden.");
        }
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        if (c.isEmpty()) {
            throw new ConductorNotFoundException("Aún no eres conductor.");
        }
        if (!vehiculoRepository.existsByIdAndIdConductor(idVehiculo, c.get().getId())) {
            throw new VehiculoNotFoundException("No tiene un vehículo con dicho id");
        }
        Vehiculo vDB = this.vehiculoRepository.findById(idVehiculo).get();
        vDB.setMarca(request.getMarca());
        vDB.setModelo(request.getModelo());
        vDB.setColor(request.getColor());
        vDB.setMatricula(request.getMatricula());
        this.vehiculoRepository.save(vDB);
        return VehiculoMapper.mapVehiculoToDto(vDB);
    }

    public String delete(int idVehiculo) {
        Optional<Conductor> c = conductorRepository.findByIdUsuario(getCurrentUsuario().getId());
        int idConductor = c.map(Conductor::getId)
                .orElseGet(() -> conductorService.autoCreate(getCurrentUsuario().getId()).getId());
        if (!vehiculoRepository.existsByIdAndIdConductor(idVehiculo, idConductor)) {
            throw new VehiculoNotFoundException("No tiene un vehículo con dicho id");
        }
        this.vehiculoRepository.deleteById(idVehiculo);
        return "Vehiculo " + idVehiculo + " eliminado con éxito.";
    }

    private Usuario getCurrentUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> u = usuarioRepository.findByUsername(username);
        if (u.isEmpty()) {
            throw new UsuarioNotFoundException("No eres usuario");
        }
        return u.get();
    }
}
