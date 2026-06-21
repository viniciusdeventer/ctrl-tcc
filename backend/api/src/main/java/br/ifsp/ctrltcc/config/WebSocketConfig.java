package br.ifsp.ctrltcc.config;

import br.ifsp.ctrltcc.security.JwtUtil;
import br.ifsp.ctrltcc.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandshakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefixo para mensagens enviadas do cliente → servidor
        registry.setApplicationDestinationPrefixes("/app");
        // Prefixo para tópicos de broadcast (servidor → clientes)
        registry.enableSimpleBroker("/topic");
    }

    /**
     * Interceptor que valida o JWT no frame STOMP CONNECT.
     * O token deve ser enviado no header STOMP: Authorization: Bearer <token>
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authHeaders = accessor.getNativeHeader("Authorization");

                    if (authHeaders == null || authHeaders.isEmpty()) {
                        throw new IllegalArgumentException("Token JWT ausente na conexão WebSocket");
                    }

                    String authHeader = authHeaders.get(0);
                    if (!authHeader.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Formato de token inválido");
                    }

                    String token = authHeader.substring(7);

                    try {
                        String email = jwtUtil.extractEmail(token);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        if (!jwtUtil.isTokenValid(token, userDetails)) {
                            throw new IllegalArgumentException("Token JWT inválido ou expirado");
                        }

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        accessor.setUser(auth);

                    } catch (Exception e) {
                        throw new IllegalArgumentException("Falha na autenticação WebSocket: " + e.getMessage());
                    }
                }

                return message;
            }
        });
    }

    /**
     * Interceptor de handshake HTTP → extrai o token da query string como fallback
     * para clientes SockJS que não suportam headers customizados no upgrade.
     * Uso: ws://host/ws?token=<jwt>
     */
    private HandshakeInterceptor jwtHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                           @NonNull ServerHttpResponse response,
                                           @NonNull WebSocketHandler wsHandler,
                                           @NonNull Map<String, Object> attributes) {
                String query = request.getURI().getQuery();
                if (query != null && query.contains("token=")) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("token=")) {
                            attributes.put("token", param.substring(6));
                        }
                    }
                }
                return true;
            }

            @Override
            public void afterHandshake(@NonNull ServerHttpRequest request,
                                       @NonNull ServerHttpResponse response,
                                       @NonNull WebSocketHandler wsHandler,
                                       @Nullable Exception exception) {}
        };
    }
}
