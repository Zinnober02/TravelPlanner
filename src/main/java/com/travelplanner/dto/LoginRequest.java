package com.travelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;  // 用户名
    
    @NotBlank(message = "密码不能为空")
    private String password;  // 密码
    
    private String ip;        // 登录IP（由后端填充）
}