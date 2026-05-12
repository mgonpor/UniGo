package com.unigo.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatResumenResponse {

    // Identificacion del chat (cada viaje es un chat de grupo)
    private Integer idViaje;
    private String origen;
    private String destino;
    private LocalDate fechaSalida;
    private String estadoViaje;

    // Conductor del viaje (el "anfitrion" del chat)
    private ConductorResponse conductor;

    // Numero total de participantes con derecho a chatear
    // (conductor + pasajeros con reserva CONFIRMADA)
    private Integer participantesCount;

    // Ultimo mensaje (para preview en la lista)
    private String ultimoMensajeTexto;
    private LocalDateTime ultimoMensajeFecha;
    private Integer ultimoMensajeIdRemitente;

    // Rol del usuario actual en este chat
    private boolean soyConductor;
}
