package com.unigo.service.mappers;

import com.unigo.persistence.entities.Reserva;
import com.unigo.service.dtos.ReservaRequest;
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

    public static Reserva mapDtoToReserva(ReservaRequest dto){
        Reserva r = new Reserva();

        r.setId(dto.getId());
        r.setIdPasajero(dto.getIdPasajero());
        r.setFechaReserva(dto.getFechaReserva());
        r.setValoracionNumerica(dto.getValoracionNumerica());
        r.setValoracionTexto(dto.getValoracionTexto());
        r.setPagado(dto.getPagado());

        // Sin idViaje ni EstadoReserva

        return r;
    }

}
