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

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Autenticacion JWT del canal STOMP entrante.
 *
 * En STOMP el Principal SOLO se asocia automaticamente a la WebSocketSession
 * cuando la auth ocurre en el handshake HTTP (HandshakeHandler). Como nosotros
 * autenticamos via cabecera del frame CONNECT, el accessor.setUser(...) solo
 * afecta a ESE mensaje; los frames SUBSCRIBE / SEND posteriores llegan sin
 * Principal y los interceptores aguas abajo (SubscriptionInterceptor, los
 * @MessageMapping) lo ven como null.
 *
 * Solucion: en CONNECT guardamos el Authentication en los sessionAttributes
 * de la WebSocketSession (compartidos entre todos los frames de esa sesion),
 * y en CUALQUIER mensaje posterior que llegue sin Principal lo restauramos
 * desde ahi.
 */
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);
    private static final String SESSION_USER_KEY = "unigo.stomp.principal";

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (command == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(command)) {
            autenticarConnect(accessor);
        } else if (accessor.getUser() == null) {
            // CONNECT ya almaceno el Principal; lo rescatamos para los demas frames.
            restaurarPrincipalDeSesion(accessor);
        }
        return message;
    }

    private void autenticarConnect(StompHeaderAccessor accessor) {
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
                throw new IllegalArgumentException("Token JWT sin subject");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtils.validateToken(token, userDetails)) {
                throw new IllegalArgumentException("Token JWT invalido");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);

            // Persistimos el Principal en los sessionAttributes de la WS session,
            // que sobreviven a lo largo de toda la conexion. Cualquier frame posterior
            // sin Principal podra restaurarlo desde aqui.
            Map<String, Object> attrs = accessor.getSessionAttributes();
            if (attrs != null) {
                attrs.put(SESSION_USER_KEY, authentication);
            }

            log.info("[STOMP CONNECT] OK usuario={} sessionId={}", username, accessor.getSessionId());
        } catch (JWTVerificationException e) {
            log.warn("[STOMP CONNECT] JWT invalido: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT invalido o expirado: " + e.getMessage());
        }
    }

    private void restaurarPrincipalDeSesion(StompHeaderAccessor accessor) {
        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) {
            return;
        }
        Object stored = attrs.get(SESSION_USER_KEY);
        if (stored instanceof Principal p) {
            accessor.setUser(p);
            log.debug("[STOMP {}] Principal restaurado desde sessionAttributes para {}",
                    accessor.getCommand(), p.getName());
        }
    }
}
