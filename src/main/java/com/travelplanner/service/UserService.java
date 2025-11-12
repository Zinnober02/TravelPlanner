package com.travelplanner.service;

import com.travelplanner.dto.AuthResponse;
import com.travelplanner.dto.LoginRequest;
import com.travelplanner.dto.RegisterRequest;
import com.travelplanner.model.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param registerRequest 注册请求参数
     * @return 认证响应
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * 用户登录
     * @param loginRequest 登录请求参数
     * @return 认证响应
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}