package com.unigo.service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensajeRequest {

    private Integer idRemitente;
//    private Integer idDestinatario;
    private Integer idViaje;
    private String texto;

}
