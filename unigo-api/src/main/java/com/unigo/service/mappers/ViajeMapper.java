package com.unigo.service.mappers;

import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.Viaje;
import com.unigo.service.dtos.ReservaResponse;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;

import java.util.ArrayList;
import java.util.List;

public class ViajeMapper {

    public static ViajeResponse mapViajeToDto(Viaje viaje) {
        ViajeResponse dto = new ViajeResponse();

        dto.setId(viaje.getId());
        dto.setIdConductor(viaje.getIdConductor());
        dto.setOrigen(viaje.getOrigen());
        dto.setDestino(viaje.getDestino());
        dto.setFechaSalida(viaje.getFechaSalida());
        dto.setPlazasDisponibles(viaje.getPlazasDisponibles());
        dto.setPrecioPorPlaza(viaje.getPrecioPlaza());
        dto.setEstadoViaje(viaje.getEstadoViaje().toString());

        List<ReservaResponse> reservas = new ArrayList<>();

        if(viaje.getReservas() != null){
            for (Reserva reserva : viaje.getReservas()) {
                reservas.add(ReservaMapper.mapReservaToDto(reserva));
            }
            dto.setReservas(reservas);
        }

        return dto;
    }

    public static Viaje mapDtoToViaje(ViajeRequest dto) {
        Viaje viaje = new Viaje();

        viaje.setId(dto.getId());
        // sin id conductor
        viaje.setOrigen(dto.getOrigen());
        viaje.setDestino(dto.getDestino());
        viaje.setFechaSalida(dto.getFechaSalida());
        viaje.setPlazasDisponibles(dto.getPlazasDisponibles());
        viaje.setPrecioPlaza(dto.getPrecioPorPlaza());
        // sin estado ni lista de reservas, lo decide el metodo

        return viaje;
    }
}
