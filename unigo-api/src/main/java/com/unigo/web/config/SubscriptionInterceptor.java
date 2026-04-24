package com.unigo.web.config;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.ViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

    @Autowired
    private ViajeService viajeService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Verificamos si la acción es una suscripción
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination(); // Ej: /topic/viaje/12

            if (destination != null && destination.startsWith("/topic/viaje/")) {
                // Extraer el ID del viaje de la URL
                int idViaje = Integer.parseInt(destination.substring(destination.lastIndexOf("/") + 1));

                // 1. El getName() ahora nos devuelve el String (ej: "juanperez")
                if (accessor.getUser() == null) {
                    throw new AccessDeniedException("Usuario no autenticado");
                }
                String username = accessor.getUser().getName();

                // 2. Buscamos el usuario en la BD
                Usuario usuario = usuarioRepository.findByUsername(username)
                        .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado en la base de datos."));

                // Validar acceso
                if (!viajeService.puedeAccederAlChat(usuario.getId(), idViaje)) {
                    throw new AccessDeniedException("No tienes permiso para unirte al chat de este viaje.");
                }
            }
        }
        return message;
    }
}
