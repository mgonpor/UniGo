package com.unigo.web.config;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.ViajeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionInterceptor.class);

    @Autowired
    private ViajeService viajeService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;
        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/topic/viaje/")) {
            // No nos interesan otras suscripciones (p.ej. broker-internal).
            return message;
        }
        int idViaje;
        try {
            idViaje = Integer.parseInt(destination.substring(destination.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            log.warn("[STOMP SUBSCRIBE] destino mal formado: {}", destination);
            throw new AccessDeniedException("Destino no valido: " + destination);
        }
        Principal user = accessor.getUser();
        if (user == null) {
            log.warn("[STOMP SUBSCRIBE] sin Principal para destino={}", destination);
            throw new AccessDeniedException("Usuario no autenticado en el canal STOMP");
        }
        String username = user.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Usuario " + username + " no encontrado"));
        boolean ok = viajeService.puedeAccederAlChat(usuario.getId(), idViaje);
        log.info("[STOMP SUBSCRIBE] usuario={} idViaje={} acceso={}", username, idViaje, ok);
        if (!ok) {
            throw new AccessDeniedException("No tienes permiso para unirte al chat de este viaje.");
        }
        return message;
    }
}
