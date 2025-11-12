package com.travelplanner.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 用户上下文工具类
 * 用于在应用程序中获取当前登录用户的信息
 */
@Component
public class UserContext {
    
    // 在实际应用中，这里应该从Spring Security上下文或ThreadLocal中获取用户ID
    // 为了演示，暂时返回一个固定的UUID
    public static UUID getCurrentUserId() {
        // 实际项目中，这里应该从安全上下文或请求头中获取用户ID
        // 例如：SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    }
}