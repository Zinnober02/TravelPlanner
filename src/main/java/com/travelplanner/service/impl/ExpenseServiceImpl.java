package com.travelplanner.service.impl;

import com.travelplanner.dto.ExpenseDTO;
import com.travelplanner.model.Expense;
import com.travelplanner.model.TravelPlan;
import com.travelplanner.repository.ExpenseRepository;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.TravelPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 费用记录服务实现类
 */
@Service
public class ExpenseServiceImpl implements ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public ExpenseDTO createExpense(UUID userId, UUID planId, ExpenseDTO expenseDTO) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 创建费用记录实体
        Expense expense = new Expense();
        BeanUtils.copyProperties(expenseDTO, expense);
        
        // 设置关联的旅行计划
        TravelPlan travelPlan = travelPlanService.getPlanEntityById(planId);
        expense.setTravelPlan(travelPlan);
        
        // 保存到数据库
        Expense savedExpense = expenseRepository.save(expense);
        
        // 转换为DTO并返回
        return convertToDTO(savedExpense);
    }
    
    @Override
    public List<ExpenseDTO> getExpenses(UUID userId, UUID planId) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 查询费用记录列表
        List<Expense> expenses = expenseRepository.findByTravelPlanId(planId);
        return expenses.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    public List<ExpenseDTO> getExpensesByType(UUID userId, UUID planId, String expenseType) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 查询指定类型的费用记录
        List<Expense> expenses = expenseRepository.findByTravelPlanIdAndExpenseType(planId, expenseType);
        return expenses.stream().map(this::convertToDTO).toList();
    }
    
    @Override
    @Transactional
    public ExpenseDTO updateExpense(UUID userId, ExpenseDTO expenseDTO) {
        // 获取费用记录
        Expense expense = expenseRepository.findById(expenseDTO.getId())
                .orElseThrow(() -> new RuntimeException("费用记录不存在"));
        
        // 验证是否属于当前用户
        UUID planId = expense.getTravelPlan().getId();
        verifyPlanOwnership(userId, planId);
        
        // 更新属性（排除planId，保持关联关系不变）
        expenseDTO.setPlanId(planId); // 确保planId一致
        BeanUtils.copyProperties(expenseDTO, expense);
        
        // 保存更新
        Expense updatedExpense = expenseRepository.save(expense);
        
        return convertToDTO(updatedExpense);
    }
    
    @Override
    @Transactional
    public void deleteExpense(UUID userId, UUID expenseId) {
        // 获取费用记录
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("费用记录不存在"));
        
        // 验证是否属于当前用户
        UUID planId = expense.getTravelPlan().getId();
        verifyPlanOwnership(userId, planId);
        
        // 删除费用记录
        expenseRepository.delete(expense);
    }
    
    @Override
    public Double calculateTotalExpenses(UUID userId, UUID planId) {
        // 验证旅行计划是否存在且属于当前用户
        verifyPlanOwnership(userId, planId);
        
        // 查询所有费用记录并计算总和
        List<Expense> expenses = expenseRepository.findByTravelPlanId(planId);
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return total.doubleValue();
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
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        BeanUtils.copyProperties(expense, dto);
        dto.setPlanId(expense.getTravelPlan().getId());
        dto.setCreatedAt(expense.getCreatedAt().toLocalDateTime().format(formatter));
        return dto;
    }
}