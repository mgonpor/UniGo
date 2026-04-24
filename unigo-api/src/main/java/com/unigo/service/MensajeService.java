package com.unigo.service;

import com.unigo.persistence.entities.Mensaje;
import com.unigo.persistence.repositories.MensajeRepository;
import com.unigo.service.dtos.MensajeRequest;
import com.unigo.service.dtos.MensajeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    public MensajeResponse guardarMensaje(MensajeRequest request) {

        Mensaje nuevoMensaje = new Mensaje();

        nuevoMensaje.setIdRemitente(request.getIdRemitente());
//        nuevoMensaje.setIdDestinatario(request.getIdDestinatario());
        nuevoMensaje.setIdViaje(request.getIdViaje());
        nuevoMensaje.setTexto(request.getTexto());
        nuevoMensaje.setFechaEnvio(LocalDateTime.now());

        Mensaje mensajeGuardado = mensajeRepository.save(nuevoMensaje);

        return MensajeResponse.builder()
                .id(mensajeGuardado.getId())
                .idRemitente(mensajeGuardado.getIdRemitente())
//                .idDestinatario(mensajeGuardado.getIdDestinatario())
                .idViaje(mensajeGuardado.getIdViaje())
                .texto(mensajeGuardado.getTexto())
                .fechaEnvio(mensajeGuardado.getFechaEnvio())
                .build();
    }

}
