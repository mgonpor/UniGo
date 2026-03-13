package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ViajeResponse {

    private Integer id;
    private Integer idConductor;
    private String origen;
    private String destino;
    private LocalDate fechaSalida;
    private Integer plazasDisponibles;
    private Double precioPorPlaza;
    private String estadoViaje;
    private List<ReservaResponse> reservas;

}
