package com.unigo.service.mappers;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.entities.Vehiculo;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.dtos.VehiculoResponse;

import java.util.ArrayList;
import java.util.List;

public class ConductorMapper {

    public static ConductorResponse mapConductorToDto(Conductor conductor) {
        ConductorResponse conductorResponse = new ConductorResponse();

        conductorResponse.setId(conductor.getId());
        conductorResponse.setIdUsuario(conductor.getIdUsuario());
        conductorResponse.setNombre(conductor.getUsuario().getNombre());
        conductorResponse.setUsername(conductor.getUsuario().getUsername());
        conductorResponse.setReputacion(conductor.getReputacion());

        List<VehiculoResponse> vehiculos = new ArrayList<>();

        if (conductor.getVehiculos() != null) {
            for (Vehiculo vehiculo : conductor.getVehiculos()) {
                vehiculos.add(VehiculoMapper.mapVehiculoToDto(vehiculo));
            }
            conductorResponse.setVehiculos(vehiculos);
        }
        return conductorResponse;
    }

}
