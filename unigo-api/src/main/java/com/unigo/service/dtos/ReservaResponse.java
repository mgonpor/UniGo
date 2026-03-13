package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReservaResponse {

    private Integer id;
    private Integer idPasajero;
    private LocalDate fechaReserva;
    private Boolean pagado;
    private String estadoReserva;

    // SIN VALORACIONES
}
