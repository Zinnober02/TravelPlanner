package com.travelplanner.service;

import com.travelplanner.dto.CreateTravelPlanRequest;
import com.travelplanner.dto.TravelPlanDTO;
import com.travelplanner.dto.UpdateTravelPlanRequest;
import com.travelplanner.model.TravelPlan;

import java.util.List;
import java.util.UUID;

/**
 * 旅行计划服务接口
 */
public interface TravelPlanService {
    
    /**
     * 创建旅行计划
     * @param userId 用户ID
     * @param request 创建请求
     * @return 创建的旅行计划
     */
    TravelPlanDTO createTravelPlan(UUID userId, CreateTravelPlanRequest request, String apiKey);
    
    /**
     * 获取用户的旅行计划列表
     * @param userId 用户ID
     * @return 旅行计划列表
     */
    List<TravelPlanDTO> getUserTravelPlans(UUID userId);
    
    /**
     * 根据ID获取旅行计划详情
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 旅行计划详情
     */
    TravelPlanDTO getTravelPlanById(UUID userId, UUID planId);
    
    /**
     * 更新旅行计划
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的旅行计划
     */
    TravelPlanDTO updateTravelPlan(UUID userId, UpdateTravelPlanRequest request, String apiKey);
    
    /**
     * 删除旅行计划
     * @param userId 用户ID
     * @param planId 计划ID
     */
    void deleteTravelPlan(UUID userId, UUID planId);
    
    /**
     * 根据目的地搜索旅行计划
     * @param userId 用户ID
     * @param destination 目的地关键词
     * @return 旅行计划列表
     */
    List<TravelPlanDTO> searchTravelPlansByDestination(UUID userId, String destination);
    
    /**
     * 获取原始实体（供其他服务内部使用）
     * @param planId 计划ID
     * @return 旅行计划实体
     */
    TravelPlan getPlanEntityById(UUID planId);

    /**
     * 根据查询文本生成旅行计划
     * @param userId 用户ID
     * @param query 查询文本
     * @param apiKey 阿里云百炼API Key
     * @return 旅行计划DTO
     */
    TravelPlanDTO createTravelPlanByQuery(UUID userId, String query, String apiKey);
}
