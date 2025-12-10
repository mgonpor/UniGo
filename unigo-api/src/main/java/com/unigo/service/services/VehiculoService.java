package com.unigo.service.services;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Vehiculo;
import com.unigo.persistence.repositories.VehiculoRepository;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.VehiculoNotFoundException;
import com.unigo.service.mappers.VehiculoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ConductorService conductorService;

    // ADMIN

    // CRUDs USER
    public List<VehiculoResponse> getVehiculosByIdConductor(int idConductor, int idUsuario) {
        if(!conductorService.isUsuario(idConductor, idUsuario)){
            throw new ConductorException("Id conductor incorrecto");
        }
        return vehiculoRepository.findAllByIdConductor(idConductor).stream()
                .map(VehiculoMapper::mapVehiculoToDto)
                .toList();
    }

    public VehiculoResponse getVehiculoByIdAndIdConductor(int idVehiculo, int idConductor, int idUsuario){
        if(!conductorService.isUsuario(idConductor, idUsuario)){
            throw new ConductorException("Id conductor incorrecto");
        }
        if(!perteneceAConductor(idVehiculo, idConductor)){
            throw new VehiculoNotFoundException("Vehiculo no encontrado");
        }
        return VehiculoMapper.mapVehiculoToDto(vehiculoRepository.findById(idVehiculo).get());
    }

    public VehiculoResponse createVehiculo(VehiculoRequest request, int idUsuario){
        Optional<Conductor> c = conductorService.findConductor(idUsuario);
        int idConductor = c.map(Conductor::getId).orElseGet(() -> conductorService.autoCreate(idUsuario).getId());

        Vehiculo v = VehiculoMapper.mapDtoToVehiculo(request);
        v.setId(0);
        v.setIdConductor(idConductor);

        return VehiculoMapper.mapVehiculoToDto(vehiculoRepository.save(v));
    }

    // AUX
    public boolean perteneceAConductor(int idVehiculo, int idConductor){
        return vehiculoRepository.existsByIdAndIdConductor(idVehiculo, idConductor);
    }

}
