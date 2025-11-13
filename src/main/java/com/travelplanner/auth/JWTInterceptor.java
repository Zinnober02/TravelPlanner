package com.travelplanner.auth;

import com.travelplanner.common.UserContext;
import com.travelplanner.exception.BusinessException;
import com.travelplanner.exception.StateCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

/**
 * JWT拦截器
 * 用于验证请求中的JWT token
 */
@Component
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * 不需要JWT验证的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/register",
            "/auth/login",
            "/static/css/**",  // 只排除CSS资源
            "/static/js/**",   // 只排除JS资源
            "/static/images/**", // 只排除图片资源
            "/static/register.html",
            "/static/login.html"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestUri = request.getRequestURI();
        log.debug("JWT拦截请求: {}", requestUri);

        // 检查是否为不需要验证的路径
        if (isExcludePath(requestUri)) {
            return true;
        }

        // 从请求头获取token
        String token = extractToken(request);
        if (token == null) {
            log.warn("未提供JWT token: {}", requestUri);
            throw new BusinessException(StateCode.UNAUTHORIZED);
        }

        // 验证token
        if (!jwtUtil.isTokenValid(token)) {
            log.warn("无效的JWT token: {}", requestUri);
            throw new BusinessException(StateCode.UNAUTHORIZED);
        }

        // 将用户信息设置到上下文
        try {
            String username = jwtUtil.extractUsername(token);
            UUID userId = jwtUtil.extractUserId(token);
            UserContext.setCurrentUserId(userId);
            UserContext.setCurrentUsername(username);
            log.debug("JWT验证通过，用户: {}, 请求: {}", username, requestUri);
        } catch (Exception e) {
            log.error("从token中提取用户信息失败: {}", e.getMessage());
            throw new BusinessException(StateCode.UNAUTHORIZED);
        }

        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理用户上下文信息，避免ThreadLocal内存泄漏
        UserContext.clear();
        log.debug("请求完成，清理用户上下文");
    }
    /**
     * 从请求头提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断路径是否为排除路径
     */
    private boolean isExcludePath(String requestUri) {
        for (String excludePath : EXCLUDE_PATHS) {
            if (excludePath.endsWith("/**")) {
                String basePath = excludePath.substring(0, excludePath.length() - 3);
                if (requestUri.startsWith(basePath)) {
                    return true;
                }
            } else if (requestUri.equals(excludePath)) {
                return true;
            }
        }
        return false;
    }
}