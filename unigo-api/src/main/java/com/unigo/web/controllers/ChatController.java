package com.unigo.web.controllers;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.MensajeService;
import com.unigo.service.dtos.MensajeRequest;
import com.unigo.service.dtos.MensajeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MessageMapping("/chat/viaje/{idViaje}")
    public void procesarMensaje(
            @DestinationVariable int idViaje,
            @Payload MensajeRequest chatMessage,
            Principal principal) {

        // 1. Obtenemos al usuario real a partir del token JWT validado previamente
        Usuario usuarioReal = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no válido"));

        // 2. Forzamos la seguridad: Sobrescribimos los datos del DTO con la verdad del servidor
        chatMessage.setIdRemitente(usuarioReal.getId());
        chatMessage.setIdViaje(idViaje); // Garantizamos que no manden mensajes a otros viajes cruzados

        // 3. Guardamos el mensaje
        MensajeResponse mensajeProcesado = mensajeService.guardarMensaje(chatMessage);

        // 4. Reenviamos el mensaje
        String destino = "/topic/viaje/" + idViaje;
        simpMessagingTemplate.convertAndSend(destino, mensajeProcesado);
    }

}
