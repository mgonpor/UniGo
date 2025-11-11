package com.unigo.service.mappers;

import com.unigo.persistence.entities.Conductor;
import com.unigo.service.dtos.ConductorRequest;
import com.unigo.service.dtos.ConductorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public abstract class ConductorMapper {

    public static final ConductorMapper INSTANCE = Mappers.getMapper(ConductorMapper.class);

    public ConductorResponse mapConductorToDto(Conductor conductor){


        ConductorResponse.ConductorResponseBuilder response =
                ConductorResponse.builder()
                    .id(conductor.getId())
                    .nombre(conductor.getNombre())
                    .nombreUsuario(conductor.getNombreUsuario());

        if(!conductor.getVehiculos().isEmpty()){

            response.vehiculos(conductor.getVehiculos()
                    .stream()
                    .map(VehiculoMapper.INSTANCE::mapVehiculoToDto)
                    .toList());
        }

        // TODO: hacer que reputacion venga de Viajes <- Reservas(valoracionNumerica)
        response.reputacion(conductor.getReputacion());

        return response.build();
    }

    // TODO: !IMPORTANT mapDtoToConductor

}
