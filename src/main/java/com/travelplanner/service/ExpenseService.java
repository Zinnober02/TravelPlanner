package com.travelplanner.service;

import com.travelplanner.dto.ExpenseDTO;

import java.util.List;
import java.util.UUID;

/**
 * 费用记录服务接口
 */
public interface ExpenseService {
    
    /**
     * 创建费用记录
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @param expenseDTO 费用记录数据
     * @return 创建的费用记录
     */
    ExpenseDTO createExpense(UUID userId, UUID planId, ExpenseDTO expenseDTO);
    
    /**
     * 获取旅行计划的所有费用记录
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @return 费用记录列表
     */
    List<ExpenseDTO> getExpenses(UUID userId, UUID planId);
    
    /**
     * 按费用类型获取费用记录
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @param expenseType 费用类型
     * @return 费用记录列表
     */
    List<ExpenseDTO> getExpensesByType(UUID userId, UUID planId, String expenseType);
    
    /**
     * 更新费用记录
     * @param userId 用户ID
     * @param expenseDTO 费用记录数据
     * @return 更新后的费用记录
     */
    ExpenseDTO updateExpense(UUID userId, ExpenseDTO expenseDTO);
    
    /**
     * 删除费用记录
     * @param userId 用户ID
     * @param expenseId 费用记录ID
     */
    void deleteExpense(UUID userId, UUID expenseId);
    
    /**
     * 计算旅行计划的总费用
     * @param userId 用户ID
     * @param planId 旅行计划ID
     * @return 总费用
     */
    Double calculateTotalExpenses(UUID userId, UUID planId);
}