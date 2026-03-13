package com.unigo.service;

import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.entities.enums.EstadoViaje;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.ViajeRepository;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.ViajeException;
import com.unigo.service.exceptions.ViajeNotFoundException;
import com.unigo.service.mappers.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private ConductorRepository conductorRepository;

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
        if (!conductorRepository.existsById(id)){
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        this.viajeRepository.deleteById(id);
        return "Viaje " + id + " eliminado con exito";
    }

}
