package com.travelplanner.dto;

import com.travelplanner.model.User;
import lombok.Data;

/**
 * 认证响应DTO
 */
@Data
public class AuthResponse {
    private boolean success;      // 是否成功
    private String message;       // 消息
    private String token;         // JWT令牌
    private User user;            // 用户信息（不包含密码）
    private Long expireTime;      // 过期时间（毫秒）
}