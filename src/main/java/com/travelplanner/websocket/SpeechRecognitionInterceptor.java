package com.travelplanner.websocket;

import com.travelplanner.auth.JWTUtil;
import com.travelplanner.common.UserContext;
import com.travelplanner.exception.BusinessException;
import com.travelplanner.exception.StateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 * WebSocket握手拦截器
 * 用于验证JWT令牌
 */
@Component
public class SpeechRecognitionInterceptor implements HandshakeInterceptor {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从查询参数中获取token
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            throw new BusinessException(StateCode.UNAUTHORIZED);
        }

        String token = query.substring(query.indexOf("token=") + 6);
        if (token.contains("&")) {
            token = token.substring(0, token.indexOf("&"));
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(StateCode.UNAUTHORIZED);
        }

        // 从token中提取用户信息
        String username = jwtUtil.extractUsername(token);
        UUID userId = jwtUtil.extractUserId(token);

        // 将用户信息存储到WebSocket会话属性中
        attributes.put("userId", userId);
        attributes.put("username", username);

        // 设置用户上下文
        UserContext.setCurrentUserId(userId);
        UserContext.setCurrentUsername(username);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的处理
    }
}