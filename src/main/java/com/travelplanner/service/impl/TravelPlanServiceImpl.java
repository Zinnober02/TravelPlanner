package com.travelplanner.service.impl;

import com.travelplanner.dto.CreateTravelPlanRequest;
import com.travelplanner.dto.TravelPlanDTO;
import com.travelplanner.dto.UpdateTravelPlanRequest;
import com.travelplanner.model.TravelPlan;
import com.travelplanner.model.User;
import com.travelplanner.repository.PlanDetailRepository;
import com.travelplanner.repository.TravelPlanRepository;
import com.travelplanner.service.TravelPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.time.temporal.ChronoUnit;

/**
 * 旅行计划服务实现类
 */
@Service
public class TravelPlanServiceImpl implements TravelPlanService {
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private PlanDetailRepository planDetailRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public TravelPlanDTO createTravelPlan(UUID userId, CreateTravelPlanRequest request) {
        TravelPlan travelPlan = new TravelPlan();
        BeanUtils.copyProperties(request, travelPlan);
        
        // 设置用户信息
        User user = new User();
        user.setId(userId);
        travelPlan.setUser(user);
        travelPlan.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        travelPlan.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // 保存到数据库
        TravelPlan savedPlan = travelPlanRepository.save(travelPlan);
        
        // 转换为DTO并返回
        return convertToDTO(savedPlan);
    }
    
    @Override
    public List<TravelPlanDTO> getUserTravelPlans(UUID userId) {
        List<TravelPlan> plans = travelPlanRepository.findByUserId(userId);
        return plans.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    public TravelPlanDTO getTravelPlanById(UUID userId, UUID planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("旅行计划不存在"));
        
        // 验证是否属于当前用户
        if (!plan.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权访问该旅行计划");
        }
        
        return convertToDTO(plan);
    }
    
    @Override
    @Transactional
    public TravelPlanDTO updateTravelPlan(UUID userId, UpdateTravelPlanRequest request) {
        TravelPlan plan = travelPlanRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("旅行计划不存在"));
        
        // 验证是否属于当前用户
        if (!plan.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改该旅行计划");
        }
        
        // 更新属性
        BeanUtils.copyProperties(request, plan);
        
        // 保存更新
        TravelPlan updatedPlan = travelPlanRepository.save(plan);
        
        return convertToDTO(updatedPlan);
    }
    
    @Override
    @Transactional
    public void deleteTravelPlan(UUID userId, UUID planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("旅行计划不存在"));
        
        // 验证是否属于当前用户
        if (!plan.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除该旅行计划");
        }
        
        // 级联删除行程详情和费用记录
        planDetailRepository.deleteByTravelPlanId(planId);
        
        // 删除旅行计划
        travelPlanRepository.delete(plan);
    }
    
    @Override
    public List<TravelPlanDTO> searchTravelPlansByDestination(UUID userId, String destination) {
        List<TravelPlan> plans = travelPlanRepository.findByUserIdAndDestinationContaining(userId, destination);
        return plans.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    public TravelPlan getPlanEntityById(UUID planId) {
        return travelPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("旅行计划不存在"));
    }
    
    /**
     * 将实体类转换为DTO
     */
    private TravelPlanDTO convertToDTO(TravelPlan plan) {
        TravelPlanDTO dto = new TravelPlanDTO();
        BeanUtils.copyProperties(plan, dto);
        
        // 计算行程天数
        long days = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        dto.setDays(days);
        
        // 格式化创建时间
        dto.setCreatedAt(plan.getCreatedAt().toLocalDateTime().format(formatter));
        
        return dto;
    }
}