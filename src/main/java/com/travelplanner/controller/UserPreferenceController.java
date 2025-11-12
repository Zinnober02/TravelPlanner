package com.travelplanner.controller;

import com.travelplanner.dto.UserPreferenceDTO;
import com.travelplanner.service.UserPreferenceService;
import com.travelplanner.common.Result;
import com.travelplanner.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 用户偏好控制器
 */
@RestController
@RequestMapping("/api/user-preferences")
public class UserPreferenceController {
    
    @Autowired
    private UserPreferenceService userPreferenceService;
    
    /**
     * 获取当前用户的偏好设置
     */
    @GetMapping
    public Result<UserPreferenceDTO> getUserPreference() {
        UUID userId = UserContext.getCurrentUserId();
        UserPreferenceDTO preference = userPreferenceService.getUserPreference(userId);
        return Result.success(preference);
    }
    
    /**
     * 保存或更新用户偏好设置
     */
    @PostMapping
    public Result<UserPreferenceDTO> saveUserPreference(@RequestBody UserPreferenceDTO preferenceDTO) {
        UUID userId = UserContext.getCurrentUserId();
        UserPreferenceDTO savedPreference = userPreferenceService.saveUserPreference(userId, preferenceDTO);
        return Result.success(savedPreference);
    }
    
    /**
     * 删除用户偏好设置
     */
    @DeleteMapping
    public Result<Void> deleteUserPreference() {
        UUID userId = UserContext.getCurrentUserId();
        userPreferenceService.deleteUserPreference(userId);
        return Result.success();
    }
}