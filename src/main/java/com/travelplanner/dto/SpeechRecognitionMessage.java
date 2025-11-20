package com.travelplanner.dto;

/**
 * 语音识别消息DTO
 * 用于前端与后端WebSocket通信
 */
public class SpeechRecognitionMessage {
    
    /**
     * 消息类型
     * start: 开始识别
     * end: 结束识别
     * result: 识别结果
     * error: 错误信息
     */
    private String type;
    
    /**
     * 识别文本（当type为result时使用）
     */
    private String text;
    
    /**
     * 错误信息（当type为error时使用）
     */
    private String message;
    
    public SpeechRecognitionMessage() {
    }
    
    public SpeechRecognitionMessage(String type) {
        this.type = type;
    }
    
    public SpeechRecognitionMessage(String type, String text) {
        this.type = type;
        this.text = text;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * 创建开始识别消息
     */
    public static SpeechRecognitionMessage createStartMessage() {
        return new SpeechRecognitionMessage("start");
    }
    
    /**
     * 创建结束识别消息
     */
    public static SpeechRecognitionMessage createEndMessage() {
        return new SpeechRecognitionMessage("end");
    }
    
    /**
     * 创建识别结果消息
     */
    public static SpeechRecognitionMessage createResultMessage(String text) {
        return new SpeechRecognitionMessage("result", text);
    }
    
    /**
     * 创建错误消息
     */
    public static SpeechRecognitionMessage createErrorMessage(String message) {
        SpeechRecognitionMessage errorMessage = new SpeechRecognitionMessage("error");
        errorMessage.setMessage(message);
        return errorMessage;
    }
}