package com.unigo.service.mappers;

import com.unigo.persistence.entities.Reserva;
import com.unigo.service.dtos.ReservaResponse;

public class ReservaMapper {

    public static ReservaResponse mapReservaToDto(Reserva reserva){
        ReservaResponse dto = new ReservaResponse();

        dto.setId(reserva.getId());
        dto.setIdPasajero(reserva.getIdPasajero());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setPagado(reserva.isPagado());
        dto.setEstadoReserva(reserva.getEstadoReserva().toString());

        return dto;
    }

}
