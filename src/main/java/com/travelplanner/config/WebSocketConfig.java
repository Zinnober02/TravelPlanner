package com.travelplanner.config;

import com.travelplanner.websocket.SpeechRecognitionInterceptor;
import com.travelplanner.websocket.SpeechRecognitionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 配置语音识别WebSocket支持
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SpeechRecognitionHandler speechRecognitionHandler;
    private final SpeechRecognitionInterceptor speechRecognitionInterceptor;

    public WebSocketConfig(SpeechRecognitionHandler speechRecognitionHandler, 
                          SpeechRecognitionInterceptor speechRecognitionInterceptor) {
        this.speechRecognitionHandler = speechRecognitionHandler;
        this.speechRecognitionInterceptor = speechRecognitionInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(speechRecognitionHandler, "/ws/speech")
                .addInterceptors(speechRecognitionInterceptor)
                .setAllowedOrigins("*");
    }
}