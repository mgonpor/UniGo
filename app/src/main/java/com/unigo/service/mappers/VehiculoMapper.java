package com.unigo.service.mappers;

import com.unigo.persistence.entities.Vehiculo;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;

public class VehiculoMapper {

    public static VehiculoResponse mapVehiculoToDto(Vehiculo vehiculo) {
        VehiculoResponse dto = new VehiculoResponse();
        dto.setId(vehiculo.getId());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setColor(vehiculo.getColor());

        return dto;
    }


    public static Vehiculo mapDtoToVehiculo(VehiculoRequest dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(dto.getId());
        vehiculo.setIdConductor(dto.getIdConductor());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setMatricula(dto.getMatricula());

        return vehiculo;
    }

}
