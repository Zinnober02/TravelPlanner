package com.travelplanner.repository;

import com.travelplanner.model.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 旅行计划仓库接口
 */
@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, UUID> {
    
    /**
     * 根据用户ID查询旅行计划列表
     * @param userId 用户ID
     * @return 旅行计划列表
     */
    List<TravelPlan> findByUserId(UUID userId);
    
    /**
     * 根据用户ID和状态查询旅行计划列表
     * @param userId 用户ID
     * @param status 状态
     * @return 旅行计划列表
     */
    List<TravelPlan> findByUserIdAndStatus(UUID userId, String status);
    
    /**
     * 根据目的地模糊查询旅行计划
     * @param userId 用户ID
     * @param destination 目的地关键词
     * @return 旅行计划列表
     */
    List<TravelPlan> findByUserIdAndDestinationContaining(UUID userId, String destination);
}
