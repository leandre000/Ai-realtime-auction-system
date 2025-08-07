// config/WebSocketSecurityConfig.java
package com.webapp.realtimeauctionbackend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {  // For Spring Security 5.x+

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/app/auction/**").authenticated()
                .simpDestMatchers("/app/bid/**").authenticated()
                .simpSubscribeDestMatchers("/topic/auction/**").authenticated()
                .simpSubscribeDestMatchers("/topic/bid/**").authenticated()
                .simpSubscribeDestMatchers("/user/queue/**").authenticated()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true; // Disable CSRF for WebSocket
    }
}