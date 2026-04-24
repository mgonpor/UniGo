package com.unigo.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MensajeResponse {

    private Integer id;
    private Integer idRemitente;
//    private Integer idDestinatario;
    private Integer idViaje;
    private String texto;
    private LocalDateTime fechaEnvio;

}
