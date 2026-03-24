package com.unigo.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReservaRequest {

    private Integer id;
    private Integer idPasajero;
    private LocalDate fechaReserva;
    private Integer valoracionNumerica;
    private String valoracionTexto;
    private Boolean pagado;
    private String estadoReserva;

}
