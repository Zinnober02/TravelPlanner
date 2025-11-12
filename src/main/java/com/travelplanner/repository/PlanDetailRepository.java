package com.travelplanner.repository;

import com.travelplanner.model.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 行程详情仓库接口
 */
@Repository
public interface PlanDetailRepository extends JpaRepository<PlanDetail, UUID> {
    
    /**
     * 根据旅行计划ID查询行程详情列表
     * @param planId 旅行计划ID
     * @return 行程详情列表
     */
    List<PlanDetail> findByTravelPlanId(UUID planId);
    
    /**
     * 根据旅行计划ID和天数查询行程详情
     * @param planId 旅行计划ID
     * @param dayNumber 第几天
     * @return 行程详情列表
     */
    List<PlanDetail> findByTravelPlanIdAndDayNumber(UUID planId, Integer dayNumber);
    
    /**
     * 根据旅行计划ID删除所有相关行程详情
     * @param planId 旅行计划ID
     */
    void deleteByTravelPlanId(UUID planId);
}
