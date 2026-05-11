package com.unigo.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {

    private Integer id;
    private Integer idPasajero;
    private LocalDate fechaReserva;
    private Integer valoracionNumerica;
    private String valoracionTexto;
    private String estadoReserva;

}
