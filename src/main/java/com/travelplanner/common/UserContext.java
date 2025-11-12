package com.travelplanner.common;

import java.util.UUID;

/**
 * 用户上下文工具类
 * 用于在应用程序中获取当前登录用户的信息
 */
public class UserContext {
    
    // 使用ThreadLocal存储用户ID和用户名
    private static final ThreadLocal<UUID> userIdThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameThreadLocal = new ThreadLocal<>();
    
    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public static void setCurrentUserId(UUID userId) {
        userIdThreadLocal.set(userId);
    }
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static UUID getCurrentUserId() {
        return userIdThreadLocal.get();
    }
    
    /**
     * 设置当前用户名
     * @param username 用户名
     */
    public static void setCurrentUsername(String username) {
        usernameThreadLocal.set(username);
    }
    
    /**
     * 获取当前用户名
     * @return 用户名
     */
    public static String getCurrentUsername() {
        return usernameThreadLocal.get();
    }
    
    /**
     * 清除当前线程的用户上下文信息
     * 用于请求结束时清理资源，避免内存泄漏
     */
    public static void clear() {
        userIdThreadLocal.remove();
        usernameThreadLocal.remove();
    }
}