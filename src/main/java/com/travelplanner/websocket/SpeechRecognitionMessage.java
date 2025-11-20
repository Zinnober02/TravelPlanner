package com.travelplanner.websocket;

/**
 * 语音识别WebSocket消息
 */
public class SpeechRecognitionMessage {
    private String type;
    private String content;
    
    public SpeechRecognitionMessage() {
    }
    
    public SpeechRecognitionMessage(String type, String content) {
        this.type = type;
        this.content = content;
    }
    
    public static SpeechRecognitionMessage createResultMessage(String result) {
        return new SpeechRecognitionMessage("result", result);
    }
    
    public static SpeechRecognitionMessage createErrorMessage(String error) {
        return new SpeechRecognitionMessage("error", error);
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}