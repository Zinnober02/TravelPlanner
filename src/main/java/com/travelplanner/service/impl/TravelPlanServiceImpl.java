package com.travelplanner.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.travelplanner.dto.CreateTravelPlanRequest;
import com.travelplanner.dto.TravelPlanDTO;
import com.travelplanner.dto.UpdateTravelPlanRequest;
import com.travelplanner.model.TravelPlan;
import com.travelplanner.model.User;
import com.travelplanner.repository.PlanDetailRepository;
import com.travelplanner.repository.TravelPlanRepository;
import com.travelplanner.service.TravelPlanService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 旅行计划服务实现类
 */
@Slf4j
@Service
public class TravelPlanServiceImpl implements TravelPlanService {
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private PlanDetailRepository planDetailRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Value("${alibaba.dashscope.api-key}")
    private String apiKey;

    @Override
    @Transactional
    public TravelPlanDTO createTravelPlan(UUID userId, CreateTravelPlanRequest request) {
        TravelPlan travelPlan = new TravelPlan();
        BeanUtils.copyProperties(request, travelPlan);

        log.info("创建旅行计划, 用户ID: {}, 请求内容: {}", userId, request);
        
        // 设置用户信息
        User user = new User();
        user.setId(userId);
        travelPlan.setUser(user);
        travelPlan.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        travelPlan.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // 调用阿里云百炼生成旅行规划
        String generatedPlan = generateTravelPlanWithAI(request);
        travelPlan.setPlanJson(generatedPlan);

        // 保存到数据库
        TravelPlan savedPlan = travelPlanRepository.save(travelPlan);
        
        // 转换为DTO并返回
        return convertToDTO(savedPlan);
    }

    /**
     * 使用阿里云百炼生成旅行规划
     * @param request 旅行计划请求
     * @return 生成的旅行规划JSON
     */
    private String generateTravelPlanWithAI(CreateTravelPlanRequest request) {
        try {
            // 设置API密钥
            Constants.apiKey = apiKey;
            
            // 构建prompt
            String prompt = buildTravelPlanPrompt(request);
            
            // 构建请求参数
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder().role("system").content("你是一位专业的旅行规划师，请根据用户提供的旅行需求，生成一份详细的结构化旅行规划。").build());
            messages.add(Message.builder().role("user").content(prompt).build());
            
            GenerationParam param = GenerationParam.builder()
                    .model("qwen-flash")
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .topP(0.8)
                    .topK(50)
                    .temperature(0.7f)
                    .build();
            
            // 创建Generation实例并调用API
            Generation gen = new Generation();
            GenerationResult result = gen.call(param);
            
            // 解析响应
            String planJson = result.getOutput().getChoices().get(0).getMessage().getContent();
            // 有可能是markdown的json代码块
            if (planJson.startsWith("```json") && planJson.endsWith("```")) {
                planJson = planJson.substring(7, planJson.length() - 3).trim();
            }
            return planJson;
        } catch (ApiException | InputRequiredException | NoApiKeyException e) {
            // 如果调用AI失败，打印错误日志并返回默认的空规划
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"生成规划失败\", \"data\": {}}";
        }
    }

    /**
     * 构建旅行规划的prompt
     * @param request 旅行计划请求
     * @return 构建好的prompt
     */
    private String buildTravelPlanPrompt(CreateTravelPlanRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请为我生成一份详细的旅行规划，以下是我的旅行需求：\n");
        prompt.append("1. 旅行目的地：")
                .append(request.getDestination())
                .append("\n");
        prompt.append("2. 旅行时间：")
                .append(request.getStartDate())
                .append(" 至 ")
                .append(request.getEndDate())
                .append("\n");

        // 计算旅行天数
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        prompt.append("3. 旅行天数：")
                .append(days)
                .append(" 天\n");

        prompt.append("4. 预算：")
                .append(request.getBudget())
                .append(" 元\n");

        prompt.append("5. 人数：")
                .append(request.getPeopleCount())
                .append(" 人\n");

        if (request.getPreferences() != null && !request.getPreferences().trim().isEmpty()) {
            prompt.append("6. 旅行偏好：")
                    .append(request.getPreferences())
                    .append("\n");
        }

        prompt.append("\n请按照以下JSON格式返回规划结果：")
                .append("\n{")
                .append("\n  \"plan\": {")
                .append("\n    \"destination\": \"目的地名称\",")
                .append("\n    \"start_date\": \"开始日期\",")
                .append("\n    \"end_date\": \"结束日期\",")
                .append("\n    \"total_days\": 天数,")
                .append("\n    \"budget\": 预算,")
                .append("\n    \"people_count\": 人数,")
                .append("\n    \"days\": [")
                .append("\n      {")
                .append("\n        \"day\": 1,")
                .append("\n        \"date\": \"日期\",")
                .append("\n        \"title\": \"当天主题\",")
                .append("\n        \"schedule\": [")
                .append("\n          {")
                .append("\n            \"time\": \"时间\",")
                .append("\n            \"activity\": \"活动内容\",")
                .append("\n            \"location\": \"地点\",")
                .append("\n            \"description\": \"详细描述\",")
                .append("\n            \"cost\": 费用")
                .append("\n          }")
                .append("\n        ]")
                .append("\n      }")
                .append("\n    ]")
                .append("\n  }")
                .append("\n}")
                .append("\n\n请严格按照上述JSON格式返回，不要添加任何额外的解释或说明，确保JSON格式正确。");

        return prompt.toString();
    }
    
    @Override
    public List<TravelPlanDTO> getUserTravelPlans(UUID userId) {
        log.info("获取用户ID为 {} 的所有旅行计划", userId);
        List<TravelPlan> plans = travelPlanRepository.findByUserId(userId);
        return plans.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    public TravelPlanDTO getTravelPlanById(UUID userId, UUID planId) {
        log.info("获取旅行计划ID为 {} 的详情", planId);
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

        // 检查是否已归档
        if ("archived".equals(plan.getStatus())) {
            throw new RuntimeException("已归档的旅行计划无法修改");
        }

        log.info("更新旅行计划: {}, 请求内容: {}", plan.getTitle(), request);
        
        // 检查是否有实际字段变化
        boolean aiFieldsChanged = false;
        
        // 比较字符串类型字段
        if (!Objects.equals(request.getTitle(), plan.getTitle())) {
            aiFieldsChanged = true;
        }
        if (!Objects.equals(request.getDestination(), plan.getDestination())) {
            aiFieldsChanged = true;
        }
        if (!Objects.equals(request.getPreferences(), plan.getPreferences())) {
            aiFieldsChanged = true;
        }
        // 比较日期类型字段
        if (!Objects.equals(request.getStartDate(), plan.getStartDate())) {
            aiFieldsChanged = true;
        }
        if (!Objects.equals(request.getEndDate(), plan.getEndDate())) {
            aiFieldsChanged = true;
        }
        // 比较整数类型字段
        if (!Objects.equals(request.getPeopleCount(), plan.getPeopleCount())) {
            aiFieldsChanged = true;
        }
        // 比较BigDecimal类型字段，使用compareTo避免精度问题
        if (request.getBudget() != null && plan.getBudget() != null) {
            if (request.getBudget().compareTo(plan.getBudget()) != 0) {
                aiFieldsChanged = true;
            }
        } else if (request.getBudget() != plan.getBudget()) {
            aiFieldsChanged = true;
        }
        boolean hasChanges = false;

        if (!Objects.equals(request.getStatus(), plan.getStatus())) {
            hasChanges = true;
        }
        
        // 如果没有任何变化，直接返回
        if (!hasChanges && !aiFieldsChanged) {
            throw new RuntimeException("旅行计划未发生任何变化，无需更新");
        }
        
        // 更新属性
        BeanUtils.copyProperties(request, plan);
        
        // 保存更新
        TravelPlan updatedPlan = travelPlanRepository.save(plan);
        
        // 仅当AI相关字段变化时重新生成行程
        if (aiFieldsChanged) {
            log.info("旅行计划ID: {} 有AI相关字段变化，需要重新生成行程", updatedPlan.getId());
            // 基于更新后的计划重新生成AI行程
            CreateTravelPlanRequest aiRequest = new CreateTravelPlanRequest();
            BeanUtils.copyProperties(updatedPlan, aiRequest);
            
            // 调用AI生成新的行程
            String newPlanJson = generateTravelPlanWithAI(aiRequest);
            
            // 更新planJson字段
            updatedPlan.setPlanJson(newPlanJson);
            
            // 保存更新后的planJson
            updatedPlan = travelPlanRepository.save(updatedPlan);

            log.info("旅行计划ID: {} 已更新", updatedPlan.getId());
        } else {
            log.info("旅行计划ID: {} 已更新, AI相关字段未变化，无需重新生成行程", updatedPlan.getId());
        }
        
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
        
        log.info("删除旅行计划ID: {}", plan.getId());
        
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