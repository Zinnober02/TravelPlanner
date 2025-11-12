package com.travelplanner.auth;

import java.util.UUID;

/**
 * 用户上下文类
 * 使用ThreadLocal存储当前线程的用户信息
 */
public class UserContext {

    /**
     * ThreadLocal存储当前登录用户ID
     */
    private static final ThreadLocal<UUID> CURRENT_USER_ID = new ThreadLocal<>();

    /**
     * ThreadLocal存储当前登录用户名
     */
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    /**
     * 设置当前登录用户ID
     */
    public static void setCurrentUserId(UUID userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * 获取当前登录用户ID
     */
    public static UUID getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 设置当前登录用户名
     */
    public static void setCurrentUsername(String username) {
        CURRENT_USERNAME.set(username);
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        return CURRENT_USERNAME.get();
    }

    /**
     * 判断用户是否已登录
     */
    public static boolean isLoggedIn() {
        return CURRENT_USER_ID.get() != null;
    }

    /**
     * 清除当前线程的用户信息
     * 必须在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }
}