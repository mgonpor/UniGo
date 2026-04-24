package com.unigo.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtChannelInterceptor jwtChannelInterceptor;
    @Autowired
    private SubscriptionInterceptor subscriptionInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
        // Registramos nuestro interceptor para los mensajes entrantes del cliente
        registration.interceptors(subscriptionInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Definimos el endpoint de conexión para el cliente frontend
        registry.addEndpoint("/ws")
                // 2. Configuramos CORS para evitar problemas si tu frontend corre en otro puerto (ej. localhost:4200 o localhost:3000)
                .setAllowedOriginPatterns("*")
                // 3. Habilitamos SockJS como plan de respaldo
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 4. Prefijos para los canales a los que el frontend se va a SUSCRIBIR para escuchar mensajes
        // "/topic" -> Usualmente para mensajes de difusión grupal (ej. el chat de un viaje)
        // "/queue" -> Usualmente para mensajes directos o privados
        registry.enableSimpleBroker("/topic", "/queue");

        // 5. Prefijo para las rutas a las que el frontend va a ENVIAR mensajes hacia el servidor
        // Estos mensajes serán interceptados por los controladores (@MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");
    }

}
