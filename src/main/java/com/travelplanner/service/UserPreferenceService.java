package com.travelplanner.service;

import com.travelplanner.dto.UserPreferenceDTO;

import java.util.UUID;

/**
 * 用户偏好服务接口
 */
public interface UserPreferenceService {
    
    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户偏好信息
     */
    UserPreferenceDTO getUserPreference(UUID userId);
    
    /**
     * 保存或更新用户偏好
     * @param userId 用户ID
     * @param preferenceDTO 用户偏好数据
     * @return 更新后的用户偏好
     */
    UserPreferenceDTO saveUserPreference(UUID userId, UserPreferenceDTO preferenceDTO);
    
    /**
     * 删除用户偏好
     * @param userId 用户ID
     */
    void deleteUserPreference(UUID userId);
}
