package com.unigo.web.controllers;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.MensajeService;
import com.unigo.service.ViajeService;
import com.unigo.service.dtos.MensajeRequest;
import com.unigo.service.dtos.MensajeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    private ViajeService viajeService;

    @MessageMapping("/chat/viaje/{idViaje}")
    public void procesarMensaje(
            @DestinationVariable int idViaje,
            @Payload MensajeRequest chatMessage,
            Principal principal) {

        // 1. Resolvemos el usuario real desde el Principal validado por el JwtChannelInterceptor
        Usuario usuarioReal = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no valido"));

        // 2. Verificamos que el usuario tenga acceso al chat de ese viaje (defensa
        //    en profundidad: el SubscriptionInterceptor ya bloquea SUBSCRIBE no
        //    autorizados, pero un cliente podria intentar SEND sin haber subscrito).
        if (!viajeService.puedeAccederAlChat(usuarioReal.getId(), idViaje)) {
            throw new AccessDeniedException("No tienes acceso al chat de este viaje");
        }

        // 3. Rechazamos mensajes vacios para no contaminar el historial
        if (chatMessage.getTexto() == null || chatMessage.getTexto().trim().isEmpty()) {
            return;
        }

        // 4. Forzamos los campos de seguridad: el remitente y el viaje son los del
        //    Principal y la URL, nunca lo que diga el cliente.
        chatMessage.setIdRemitente(usuarioReal.getId());
        chatMessage.setIdViaje(idViaje);

        // 5. Persistir y reenviar a todos los suscritos al topic del viaje
        MensajeResponse mensajeProcesado = mensajeService.guardarMensaje(chatMessage);
        simpMessagingTemplate.convertAndSend("/topic/viaje/" + idViaje, mensajeProcesado);
    }
}
