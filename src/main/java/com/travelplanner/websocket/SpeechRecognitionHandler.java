package com.travelplanner.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.auth.UserContext;
import com.travelplanner.dto.SpeechRecognitionMessage;
import com.travelplanner.service.SpeechRecognitionService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器
 * 处理语音识别相关的WebSocket连接和消息
 */
@Slf4j
@Component
public class SpeechRecognitionHandler implements WebSocketHandler {
    // 存储会话ID与用户ID的映射
    private final Map<String, UUID> sessionUserMap = new ConcurrentHashMap<>();
    
    // 存储会话ID与讯飞WebSocket连接的映射
    private final Map<String, SpeechRecognitionService> sessionServiceMap = new ConcurrentHashMap<>();
    
    @Autowired
    private SpeechRecognitionService speechRecognitionService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("与web端的WebSocket连接建立: {}", session.getId());
        
        // 从会话属性中获取用户信息
        var attributes = session.getAttributes();
        var userId = (UUID) attributes.get("userId");
        var username = (String) attributes.get("username");
        
        if (userId == null) {
            log.error("无法获取用户ID, 关闭连接: {}", session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        
        // 存储会话与用户的映射
        sessionUserMap.put(session.getId(), userId);
        
        // 设置用户上下文
        UserContext.setCurrentUserId(userId);
        UserContext.setCurrentUsername(username);
        
        // 创建语音识别服务实例
        SpeechRecognitionService serviceInstance = speechRecognitionService.createInstance();
        sessionServiceMap.put(session.getId(), serviceInstance);
        
        log.info("用户 {} 的语音识别服务已初始化", username);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.debug("收到web端的消息: {}, 类型: {}", session.getId(), message.getClass().getSimpleName());
        
        // 获取用户ID
        var userId = sessionUserMap.get(session.getId());
        if (userId == null) {
            log.error("无法获取用户ID, 关闭连接: {}", session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        
        // 设置用户上下文
        UserContext.setCurrentUserId(userId);
        
        // 获取语音识别服务实例
        var serviceInstance = sessionServiceMap.get(session.getId());
        if (serviceInstance == null) {
            log.error("无法获取语音识别服务实例, 关闭连接: {}", session.getId());
            session.close(CloseStatus.SERVER_ERROR);
            return;
        }
        
        try {
            if (message instanceof TextMessage) {
                // 处理文本消息（控制消息）
                String payload = ((TextMessage) message).getPayload();
                handleTextMessage(session, payload, serviceInstance);
            } else if (message instanceof BinaryMessage) {
                // 处理二进制消息（音频数据）
                byte[] payload = ((BinaryMessage) message).getPayload().array();
                handleBinaryMessage(session, payload, serviceInstance);
            }
        } catch (Exception e) {
            log.error("处理消息时发生错误: {}", e.getMessage(), e);
            sendErrorMessage(session, "处理消息时发生错误: " + e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: {}", session.getId(), exception);
        cleanupSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接关闭: {}, 状态: {}", session.getId(), closeStatus);
        cleanupSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 处理文本消息
     */
    private void handleTextMessage(WebSocketSession session, String payload, SpeechRecognitionService serviceInstance) 
            throws IOException {
        // 解析控制消息
        SpeechRecognitionMessage message;
        try {
            message = objectMapper.readValue(payload, SpeechRecognitionMessage.class);
        } catch (Exception e) {
            log.error("解析消息失败", e);
            sendErrorMessage(session, "消息格式错误");
            return;
        }
        
        if ("start".equals(message.getType())) {
            // 开始语音识别
            serviceInstance.startRecognition(
                result -> sendRecognitionResult(session, result),
                error -> sendErrorMessage(session, error)
            );
        } else if ("end".equals(message.getType())) {
            // 结束语音识别
            serviceInstance.endRecognition();
        }
    }
    
    /**
     * 处理二进制消息（音频数据）
     */
    private void handleBinaryMessage(WebSocketSession session, byte[] payload, SpeechRecognitionService serviceInstance) {
        // 发送音频数据到讯飞API
        serviceInstance.sendAudioData(payload);
    }
    
    /**
     * 发送识别结果
     */
    private void sendRecognitionResult(WebSocketSession session, String result) {
        try {
            SpeechRecognitionMessage message = SpeechRecognitionMessage.createResultMessage(result);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error("返回识别结果到web端时发生错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送错误消息
     */
    private void sendErrorMessage(WebSocketSession session, String error) {
        try {
            SpeechRecognitionMessage message = SpeechRecognitionMessage.createErrorMessage(error);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error("发送错误消息时发生错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 清理会话资源
     */
    private void cleanupSession(WebSocketSession session) {
        String sessionId = session.getId();
        
        // 清理语音识别服务
        SpeechRecognitionService serviceInstance = sessionServiceMap.remove(sessionId);
        if (serviceInstance != null) {
            serviceInstance.cleanup();
        }
        
        // 清理会话映射
        sessionUserMap.remove(sessionId);
    }
}