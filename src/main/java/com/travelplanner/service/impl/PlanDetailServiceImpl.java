package com.travelplanner.service.impl;

import com.travelplanner.dto.PlanDetailDTO;
import com.travelplanner.model.PlanDetail;
import com.travelplanner.model.TravelPlan;
import com.travelplanner.repository.PlanDetailRepository;
import com.travelplanner.service.PlanDetailService;
import com.travelplanner.service.TravelPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 行程详情服务实现类
 */
@Service
public class PlanDetailServiceImpl implements PlanDetailService {
    
    @Autowired
    private PlanDetailRepository planDetailRepository;
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    @Override
    @Transactional
    public PlanDetailDTO createPlanDetail(UUID userId, UUID planId, PlanDetailDTO planDetailDTO) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 创建行程详情实体
        PlanDetail planDetail = new PlanDetail();
        BeanUtils.copyProperties(planDetailDTO, planDetail);
        
        // 设置关联的旅行计划
        TravelPlan travelPlan = travelPlanService.getPlanEntityById(planId);
        planDetail.setTravelPlan(travelPlan);
        
        // 保存到数据库
        PlanDetail savedDetail = planDetailRepository.save(planDetail);
        
        // 转换为DTO并返回
        return convertToDTO(savedDetail);
    }
    
    @Override
    public List<PlanDetailDTO> getPlanDetails(UUID userId, UUID planId) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 查询行程详情列表
        List<PlanDetail> details = planDetailRepository.findByTravelPlanId(planId);
        return details.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    public List<PlanDetailDTO> getPlanDetailsByDay(UUID userId, UUID planId, Integer dayNumber) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 查询指定天数的行程详情
        List<PlanDetail> details = planDetailRepository.findByTravelPlanIdAndDayNumber(planId, dayNumber);
        return details.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    @Transactional
    public PlanDetailDTO updatePlanDetail(UUID userId, PlanDetailDTO planDetailDTO) {
        // 获取行程详情
        PlanDetail planDetail = planDetailRepository.findById(planDetailDTO.getId())
                .orElseThrow(() -> new RuntimeException("行程详情不存在"));
        
        // 验证是否属于当前用户
        UUID planId = planDetail.getTravelPlan().getId();
        verifyPlanOwnership(userId, planId);
        
        // 更新属性（排除planId，保持关联关系不变）
        planDetailDTO.setPlanId(planId); // 确保planId一致
        BeanUtils.copyProperties(planDetailDTO, planDetail);
        
        // 保存更新
        PlanDetail updatedDetail = planDetailRepository.save(planDetail);
        
        return convertToDTO(updatedDetail);
    }
    
    @Override
    @Transactional
    public void deletePlanDetail(UUID userId, UUID detailId) {
        // 获取行程详情
        PlanDetail planDetail = planDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("行程详情不存在"));
        
        // 验证是否属于当前用户
        UUID planId = planDetail.getTravelPlan().getId();
        verifyPlanOwnership(userId, planId);
        
        // 删除行程详情
        planDetailRepository.delete(planDetail);
    }
    
    /**
     * 验证旅行计划是否属于当前用户
     */
    private void verifyPlanOwnership(UUID userId, UUID planId) {
        TravelPlan plan = travelPlanService.getPlanEntityById(planId);
        if (!plan.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权访问该旅行计划");
        }
    }
    
    /**
     * 将实体类转换为DTO
     */
    private PlanDetailDTO convertToDTO(PlanDetail planDetail) {
        PlanDetailDTO dto = new PlanDetailDTO();
        BeanUtils.copyProperties(planDetail, dto);
        dto.setPlanId(planDetail.getTravelPlan().getId());
        return dto;
    }
}