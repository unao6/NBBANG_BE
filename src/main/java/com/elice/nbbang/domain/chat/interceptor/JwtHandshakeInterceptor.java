package com.elice.nbbang.domain.chat.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String accessToken = servletRequest.getHeader("access");

            log.info("WebSocket Handshake - Extracted Access Token: {}", accessToken);

            // 로그나 다른 필요한 검증을 이곳에서 수행합니다.
            if (accessToken == null) {
                log.warn("Access token is missing during WebSocket handshake");
            }
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
