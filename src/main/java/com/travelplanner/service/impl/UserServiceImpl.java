package com.travelplanner.service.impl;

import com.travelplanner.auth.JWTUtil;
import com.travelplanner.auth.UserContext;
import com.travelplanner.dto.AuthResponse;
import com.travelplanner.dto.LoginRequest;
import com.travelplanner.dto.RegisterRequest;
import com.travelplanner.exception.BusinessException;
import com.travelplanner.exception.ErrorCode;
import com.travelplanner.model.User;
import com.travelplanner.repository.UserRepository;
import com.travelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 检查邮箱是否已存在
        if (existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        // 检查密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "两次输入的密码不一致");
        }

        AuthResponse response = new AuthResponse();

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());

        // 保存用户
        user = userRepository.save(user);

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 设置响应
        response.setSuccess(true);
        response.setMessage("注册成功");
        response.setToken(token);
        response.setUser(user);
        response.setExpireTime(jwtUtil.getExpirationTime(token));

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // 查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getUsername()).orElse(null));

        // 检查用户是否存在且密码是否正确
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        AuthResponse response = new AuthResponse();

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 记录当前用户ID
        UserContext.setCurrentUserId(user.getId());

        // 设置响应
        response.setSuccess(true);
        response.setMessage("登录成功");
        response.setToken(token);
        response.setUser(user);
        response.setExpireTime(jwtUtil.getExpirationTime(token));

        return response;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}