package com.travelplanner.exception;

import lombok.Getter;

/**
 * 错误码枚举类
 * 定义系统中的标准错误码
 */
@Getter
public enum StateCode {

    // 通用错误码
    SUCCESS(0, "success"),
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    
    // 用户相关错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    EMAIL_EXISTS(1003, "邮箱已存在"),
    LOGIN_FAILED(1004, "登录失败，用户名或密码错误"),
    INVALID_TOKEN(1005, "无效的令牌"),
    TOKEN_EXPIRED(1006, "令牌已过期"),
    
    // 旅行计划相关错误码
    PLAN_NOT_FOUND(2001, "旅行计划不存在"),
    PLAN_CREATE_FAILED(2002, "创建旅行计划失败"),
    PLAN_UPDATE_FAILED(2003, "更新旅行计划失败"),
    PLAN_DELETE_FAILED(2004, "删除旅行计划失败"),
    NO_PERMISSION(2005, "无权限操作此资源"),
    
    // AI服务相关错误码
    AI_SERVICE_ERROR(3001, "AI服务调用失败"),
    AI_SERVICE_TIMEOUT(3002, "AI服务超时"),
    
    // Supabase相关错误码
    SUPABASE_ERROR(4001, "Supabase服务调用失败"),
    SUPABASE_NETWORK_ERROR(4002, "网络错误，请检查连接"),
    
    // 语音识别相关错误码
    SPEECH_RECOGNITION_ERROR(5001, "语音识别失败"),
    INVALID_AUDIO_FILE(5002, "无效的音频文件"),
    
    // 地图服务相关错误码
    MAP_SERVICE_ERROR(6001, "地图服务调用失败");
    
    private final int code;
    private final String message;
    
    StateCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}