package com.unigo.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Solo nos interesa interceptar el momento de la conexión inicial
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // Extraemos las cabeceras nativas enviadas por el cliente STOMP
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String bearerToken = authorization.get(0);

                if (bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);
                    String username = jwtUtils.extractUsername(token);

                    // 2. Validar el token
                    if (username != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtUtils.validateToken(token, userDetails)) {

                            // 3. (Mejora) Ahora pasamos el userDetails completo y sus roles (authorities)
                            // al contexto de seguridad, en lugar de pasar solo el String del username
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            accessor.setUser(authentication);
                        } else {
                            throw new IllegalArgumentException("Token JWT inválido o expirado");
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Falta la cabecera de Autorización en la conexión STOMP");
            }
        }
        return message;
    }
}
