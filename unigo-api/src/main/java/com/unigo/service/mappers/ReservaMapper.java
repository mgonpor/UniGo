package com.unigo.service.mappers;

import com.unigo.persistence.entities.Reserva;
import com.unigo.service.dtos.ReservaRequest;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.dtos.ViajeResponse;

public class ReservaMapper {

    public static ReservaResponse mapReservaToDto(Reserva reserva){
        ReservaResponse dto = new ReservaResponse();

        dto.setId(reserva.getId());
        dto.setIdPasajero(reserva.getIdPasajero());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setPagado(reserva.isPagado());
        dto.setEstadoReserva(reserva.getEstadoReserva().toString());

        if (reserva.getPasajero() != null) {
            dto.setPasajero(PasajeroMapper.mapPasajeroToDto(reserva.getPasajero()));
        }

        if (reserva.getViaje() != null) {
            ViajeResponse vr = new ViajeResponse();
            vr.setId(reserva.getViaje().getId());
            vr.setIdConductor(reserva.getViaje().getIdConductor());
            vr.setOrigen(reserva.getViaje().getOrigen());
            vr.setDestino(reserva.getViaje().getDestino());
            vr.setFechaSalida(reserva.getViaje().getFechaSalida());
            vr.setHoraSalida(reserva.getViaje().getHoraSalida());
            vr.setOrigenCoords(reserva.getViaje().getOrigenCoords());
            vr.setDestinoCoords(reserva.getViaje().getDestinoCoords());
            vr.setPlazasDisponibles(reserva.getViaje().getPlazasDisponibles());
            vr.setPrecioPorPlaza(reserva.getViaje().getPrecioPlaza());
            vr.setEstadoViaje(reserva.getViaje().getEstadoViaje().toString());
            if (reserva.getViaje().getConductor() != null) {
                vr.setConductor(ConductorMapper.mapConductorToDto(reserva.getViaje().getConductor()));
            }
            dto.setViaje(vr);
        }

        return dto;
    }

    public static Reserva mapDtoToReserva(ReservaRequest dto){
        Reserva r = new Reserva();

        r.setId(dto.getId());
        r.setIdPasajero(dto.getIdPasajero());
        r.setFechaReserva(dto.getFechaReserva());

        // Sin idViaje ni EstadoReserva

        return r;
    }

}
