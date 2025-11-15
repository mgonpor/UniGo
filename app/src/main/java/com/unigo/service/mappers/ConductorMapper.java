package com.unigo.service.mappers;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Vehiculo;
import com.unigo.service.dtos.ConductorRequest;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.dtos.VehiculoResponse;

import java.util.ArrayList;
import java.util.List;

public class ConductorMapper {

    // Para el create
    public static Conductor mapDtoToConductor(ConductorRequest conductorRequest){
        Conductor conductor = new Conductor();

        conductor.setNombre(conductorRequest.getNombre());
        conductor.setNombreUsuario(conductorRequest.getNombreUsuario());
        conductor.setEmail(conductorRequest.getEmail());
        conductor.setPassword(conductorRequest.getPassword());

        conductor.setReputacion(0); // Le damos reputación 0 para empezar
        conductor.setVehiculos(new ArrayList<>());
        conductor.setViajes(new ArrayList<>());

        return conductor;
    }

    public static ConductorResponse mapConductorToDto(Conductor conductor) {
        ConductorResponse conductorResponse = new ConductorResponse();

        conductorResponse.setId(conductor.getId());
        conductorResponse.setNombre(conductor.getNombre());
        conductorResponse.setNombreUsuario(conductor.getNombreUsuario());
        conductorResponse.setReputacion(conductor.getReputacion());

        List<VehiculoResponse> vehiculos = new ArrayList<>();
        for (Vehiculo vehiculo : conductor.getVehiculos()) {
            vehiculos.add(VehiculoMapper.mapVehiculoToDto(vehiculo));
        }

        conductorResponse.setVehiculos(vehiculos);

        return conductorResponse;
    }

}
