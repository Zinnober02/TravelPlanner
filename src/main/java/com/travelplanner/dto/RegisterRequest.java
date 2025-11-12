package com.travelplanner.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;  // 用户名
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;  // 密码
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword; // 确认密码
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;     // 邮箱
    
    private String phone;     // 手机号
    
    private String nickname;  // 昵称
}