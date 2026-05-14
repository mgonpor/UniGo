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
        dto.setHoraSalida(viaje.getHoraSalida());
        dto.setOrigenCoords(viaje.getOrigenCoords());
        dto.setDestinoCoords(viaje.getDestinoCoords());
        dto.setPlazasDisponibles(viaje.getPlazasDisponibles());
        dto.setPrecioPorPlaza(viaje.getPrecioPlaza());
        dto.setEstadoViaje(viaje.getEstadoViaje().toString());

        if (viaje.getConductor() != null) {
            dto.setConductor(ConductorMapper.mapConductorToDto(viaje.getConductor()));
        }

        if (viaje.getVehiculo() != null) {
            dto.setVehiculo(VehiculoMapper.mapVehiculoToDto(viaje.getVehiculo()));
        }

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

        // sin id ni id conductor
        viaje.setOrigen(dto.getOrigen());
        viaje.setDestino(dto.getDestino());
        viaje.setFechaSalida(dto.getFechaSalida());
        viaje.setHoraSalida(dto.getHoraSalida());
        viaje.setOrigenCoords(dto.getOrigenCoords());
        viaje.setDestinoCoords(dto.getDestinoCoords());
        viaje.setPlazasDisponibles(dto.getPlazasDisponibles());
        viaje.setPrecioPlaza(dto.getPrecioPorPlaza());
        viaje.setIdVehiculo(dto.getIdVehiculo());
        // sin estado ni lista de reservas, lo decide el metodo

        return viaje;
    }
}
