package com.unigo.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// En UniGO una "valoracion" se materializa como (valoracionNumerica + valoracionTexto)
// guardada dentro de una Reserva CONFIRMADA y completada. Este DTO es la vista
// agregada que consume la UI para "valoraciones recibidas / dadas".
@Getter
@Setter
@Builder
public class ValoracionResponse {

    private Integer idReserva;
    private Integer idViaje;
    private Integer puntuacion;
    private String comentario;
    private LocalDate fecha;          // fechaSalida del viaje (= fecha real de la valoracion implicita)

    private String origen;
    private String destino;

    // El "otro" en la valoracion. Para una recibida -> el pasajero que valoro.
    // Para una dada -> el conductor valorado.
    private Integer otroIdUsuario;
    private String otroNombre;
    private String otroUsername;
}
