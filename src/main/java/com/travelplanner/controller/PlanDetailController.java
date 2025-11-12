package com.travelplanner.controller;

import com.travelplanner.dto.PlanDetailDTO;
import com.travelplanner.service.PlanDetailService;
import com.travelplanner.common.Result;
import com.travelplanner.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 行程详情控制器
 */
@RestController
@RequestMapping("/api/plan-details")
public class PlanDetailController {
    
    @Autowired
    private PlanDetailService planDetailService;
    
    /**
     * 创建行程详情
     */
    @PostMapping
    public Result<PlanDetailDTO> createPlanDetail(@RequestBody PlanDetailDTO planDetailDTO) {
        UUID userId = UserContext.getCurrentUserId();
        PlanDetailDTO createdDetail = planDetailService.createPlanDetail(userId, planDetailDTO.getPlanId(), planDetailDTO);
        return Result.success(createdDetail);
    }
    
    /**
     * 获取旅行计划的所有行程详情
     */
    @GetMapping("/plan/{planId}")
    public Result<List<PlanDetailDTO>> getPlanDetails(@PathVariable("planId") UUID planId) {
        UUID userId = UserContext.getCurrentUserId();
        List<PlanDetailDTO> details = planDetailService.getPlanDetails(userId, planId);
        return Result.success(details);
    }
    
    /**
     * 获取指定天数的行程详情
     */
    @GetMapping("/plan/{planId}/day/{dayNumber}")
    public Result<List<PlanDetailDTO>> getPlanDetailsByDay(
            @PathVariable("planId") UUID planId, 
            @PathVariable("dayNumber") Integer dayNumber) {
        UUID userId = UserContext.getCurrentUserId();
        List<PlanDetailDTO> details = planDetailService.getPlanDetailsByDay(userId, planId, dayNumber);
        return Result.success(details);
    }
    
    /**
     * 更新行程详情
     */
    @PutMapping
    public Result<PlanDetailDTO> updatePlanDetail(@RequestBody PlanDetailDTO planDetailDTO) {
        UUID userId = UserContext.getCurrentUserId();
        PlanDetailDTO updatedDetail = planDetailService.updatePlanDetail(userId, planDetailDTO);
        return Result.success(updatedDetail);
    }
    
    /**
     * 删除行程详情
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePlanDetail(@PathVariable("id") UUID id) {
        UUID userId = UserContext.getCurrentUserId();
        planDetailService.deletePlanDetail(userId, id);
        return Result.success();
    }
}