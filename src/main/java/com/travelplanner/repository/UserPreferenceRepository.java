package com.travelplanner.repository;

import com.travelplanner.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户偏好仓库接口
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {
    
    /**
     * 根据用户ID查询用户偏好
     * @param userId 用户ID
     * @return 用户偏好信息
     */
    Optional<UserPreference> findByUserId(UUID userId);
    
    /**
     * 删除指定用户的偏好信息
     * @param userId 用户ID
     */
    void deleteByUserId(UUID userId);
}
