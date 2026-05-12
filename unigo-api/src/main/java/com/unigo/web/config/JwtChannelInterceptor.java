package com.unigo.web.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Usamos wrap() porque siempre devuelve un accessor valido sobre el
        // mensaje (a diferencia de getAccessor() que puede devolver null si
        // el tipo no coincide). wrap() crea una vista mutable apta para
        // setUser, que es lo que necesitamos para propagar el Principal.
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        List<String> authorization = accessor.getNativeHeader("Authorization");
        if (authorization == null || authorization.isEmpty()) {
            log.warn("[STOMP CONNECT] sin cabecera Authorization");
            throw new IllegalArgumentException("Falta la cabecera de Autorizacion en la conexion STOMP");
        }
        String bearerToken = authorization.get(0);
        if (!bearerToken.startsWith("Bearer ")) {
            log.warn("[STOMP CONNECT] Authorization mal formada");
            throw new IllegalArgumentException("Cabecera Authorization mal formada");
        }
        String token = bearerToken.substring(7);

        try {
            String username = jwtUtils.extractUsername(token);
            if (username == null) {
                log.warn("[STOMP CONNECT] token sin subject");
                throw new IllegalArgumentException("Token JWT sin subject");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtils.validateToken(token, userDetails)) {
                log.warn("[STOMP CONNECT] token invalido para {}", username);
                throw new IllegalArgumentException("Token JWT invalido");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
            accessor.setLeaveMutable(true);
            log.info("[STOMP CONNECT] OK usuario={}", username);

            // Reemplazamos el mensaje con uno que conserve el accessor mutable,
            // necesario para que el Principal viaje a los siguientes mensajes.
            return org.springframework.messaging.support.MessageBuilder
                    .createMessage(message.getPayload(), accessor.getMessageHeaders());
        } catch (JWTVerificationException e) {
            log.warn("[STOMP CONNECT] JWT invalido: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT invalido o expirado: " + e.getMessage());
        }
    }
}
