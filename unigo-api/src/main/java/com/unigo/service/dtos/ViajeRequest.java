package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ViajeRequest {

    private Integer id;
    private String origen;
    private String destino;
    private LocalDate fechaSalida;
    private java.time.LocalTime horaSalida;
    private String origenCoords;
    private String destinoCoords;
    private Integer plazasDisponibles;
    private Double precioPorPlaza;
    private Integer idVehiculo;
}
