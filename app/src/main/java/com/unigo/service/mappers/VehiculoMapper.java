package com.unigo.service.mappers;

import com.unigo.persistence.entities.Vehiculo;
import com.unigo.service.dtos.VehiculoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public abstract class VehiculoMapper {

    public static final VehiculoMapper INSTANCE = Mappers.getMapper(VehiculoMapper.class);

    public VehiculoResponse mapVehiculoToDto(Vehiculo vehiculo) {

        return VehiculoResponse.builder()
                .id(vehiculo.getId())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .color(vehiculo.getColor())
                .build();
    }

    //TODO: mapDtoToVehiculo

}
