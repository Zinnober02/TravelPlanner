package com.travelplanner.controller;

import com.travelplanner.dto.CreateTravelPlanRequest;
import com.travelplanner.dto.TravelPlanDTO;
import com.travelplanner.dto.UpdateTravelPlanRequest;
import com.travelplanner.service.TravelPlanService;
import com.travelplanner.common.Result;
import com.travelplanner.common.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 旅行计划控制器
 */
@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    /**
     * 创建旅行计划
     */
    @PostMapping
    public Result<TravelPlanDTO> createTravelPlan(@Valid @RequestBody CreateTravelPlanRequest request) {
        UUID userId = UserContext.getCurrentUserId();
        TravelPlanDTO travelPlan = travelPlanService.createTravelPlan(userId, request);
        return Result.success(travelPlan);
    }
    
    /**
     * 获取用户的旅行计划列表
     */
    @GetMapping
    public Result<List<TravelPlanDTO>> getUserTravelPlans() {
        UUID userId = UserContext.getCurrentUserId();
        List<TravelPlanDTO> plans = travelPlanService.getUserTravelPlans(userId);
        return Result.success(plans);
    }
    
    /**
     * 根据ID获取旅行计划详情
     */
    @GetMapping("/{id}")
    public Result<TravelPlanDTO> getTravelPlanById(@PathVariable("id") UUID id) {
        UUID userId = UserContext.getCurrentUserId();
        TravelPlanDTO plan = travelPlanService.getTravelPlanById(userId, id);
        return Result.success(plan);
    }
    
    /**
     * 更新旅行计划
     */
    @PutMapping
    public Result<TravelPlanDTO> updateTravelPlan(@Valid @RequestBody UpdateTravelPlanRequest request) {
        UUID userId = UserContext.getCurrentUserId();
        TravelPlanDTO plan = travelPlanService.updateTravelPlan(userId, request);
        return Result.success(plan);
    }
    
    /**
     * 删除旅行计划
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTravelPlan(@PathVariable("id") UUID id) {
        UUID userId = UserContext.getCurrentUserId();
        travelPlanService.deleteTravelPlan(userId, id);
        return Result.success();
    }
    
    /**
     * 根据目的地搜索旅行计划
     */
    @GetMapping("/search")
    public Result<List<TravelPlanDTO>> searchTravelPlans(@RequestParam("destination") String destination) {
        UUID userId = UserContext.getCurrentUserId();
        List<TravelPlanDTO> plans = travelPlanService.searchTravelPlansByDestination(userId, destination);
        return Result.success(plans);
    }
}