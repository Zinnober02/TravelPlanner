package com.travelplanner.service;

import com.travelplanner.dto.PlanDetailDTO;

import java.util.List;
import java.util.UUID;

/**
 * 行程详情服务接口
 */
public interface PlanDetailService {
    
    /**
     * 创建行程详情
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @param planDetailDTO 行程详情数据
     * @return 创建的行程详情
     */
    PlanDetailDTO createPlanDetail(UUID userId, UUID planId, PlanDetailDTO planDetailDTO);
    
    /**
     * 获取旅行计划的所有行程详情
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @return 行程详情列表
     */
    List<PlanDetailDTO> getPlanDetails(UUID userId, UUID planId);
    
    /**
     * 获取指定天数的行程详情
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @param dayNumber 天数
     * @return 行程详情列表
     */
    List<PlanDetailDTO> getPlanDetailsByDay(UUID userId, UUID planId, Integer dayNumber);
    
    /**
     * 更新行程详情
     * @param userId 用户ID
     * @param planDetailDTO 行程详情数据
     * @return 更新后的行程详情
     */
    PlanDetailDTO updatePlanDetail(UUID userId, PlanDetailDTO planDetailDTO);
    
    /**
     * 删除行程详情
     * @param userId 用户ID
     * @param detailId 行程详情ID
     */
    void deletePlanDetail(UUID userId, UUID detailId);
}
