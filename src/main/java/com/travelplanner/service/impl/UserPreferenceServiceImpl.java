package com.travelplanner.service.impl;

import com.travelplanner.dto.UserPreferenceDTO;
import com.travelplanner.model.User;
import com.travelplanner.model.UserPreference;
import com.travelplanner.repository.UserPreferenceRepository;
import com.travelplanner.service.UserPreferenceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 用户偏好服务实现类
 */
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Override
    public UserPreferenceDTO getUserPreference(UUID userId) {
        // 查询用户偏好，如果不存在返回空的DTO
        return userPreferenceRepository.findByUserId(userId)
                .map(this::convertToDTO)
                .orElseGet(UserPreferenceDTO::new);
    }
    
    @Override
    @Transactional
    public UserPreferenceDTO saveUserPreference(UUID userId, UserPreferenceDTO preferenceDTO) {
        // 查找现有偏好，如果不存在则创建新的
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserPreference newPreference = new UserPreference();
                    User user = new User();
                    user.setId(userId);
                    newPreference.setUser(user);
                    return newPreference;
                });
        
        // 更新属性
        BeanUtils.copyProperties(preferenceDTO, preference);
        
        // 保存更新
        UserPreference savedPreference = userPreferenceRepository.save(preference);
        
        return convertToDTO(savedPreference);
    }
    
    @Override
    @Transactional
    public void deleteUserPreference(UUID userId) {
        // 删除用户偏好
        userPreferenceRepository.deleteByUserId(userId);
    }
    
    /**
     * 将实体类转换为DTO
     */
    private UserPreferenceDTO convertToDTO(UserPreference preference) {
        UserPreferenceDTO dto = new UserPreferenceDTO();
        BeanUtils.copyProperties(preference, dto);
        return dto;
    }
}