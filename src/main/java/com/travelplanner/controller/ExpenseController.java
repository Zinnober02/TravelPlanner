package com.travelplanner.controller;

import com.travelplanner.dto.ExpenseDTO;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.common.Result;
import com.travelplanner.common.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 费用记录控制器
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    /**
     * 创建费用记录
     */
    @PostMapping
    public Result<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        UUID userId = UserContext.getCurrentUserId();
        ExpenseDTO createdExpense = expenseService.createExpense(userId, expenseDTO.getPlanId(), expenseDTO);
        return Result.success(createdExpense);
    }
    
    /**
     * 获取旅行计划的所有费用记录
     */
    @GetMapping("/plan/{planId}")
    public Result<List<ExpenseDTO>> getExpenses(@PathVariable("planId") UUID planId) {
        UUID userId = UserContext.getCurrentUserId();
        List<ExpenseDTO> expenses = expenseService.getExpenses(userId, planId);
        return Result.success(expenses);
    }
    
    /**
     * 按类型获取费用记录
     */
    @GetMapping("/plan/{planId}/type/{type}")
    public Result<List<ExpenseDTO>> getExpensesByType(
            @PathVariable("planId") UUID planId, 
            @PathVariable("type") String type) {
        UUID userId = UserContext.getCurrentUserId();
        List<ExpenseDTO> expenses = expenseService.getExpensesByType(userId, planId, type);
        return Result.success(expenses);
    }
    
    /**
     * 获取旅行计划的总费用
     */
    @GetMapping("/plan/{planId}/total")
    public Result<Double> getTotalExpenses(@PathVariable("planId") UUID planId) {
        UUID userId = UserContext.getCurrentUserId();
        Double total = expenseService.calculateTotalExpenses(userId, planId);
        return Result.success(total);
    }
    
    /**
     * 更新费用记录
     */
    @PutMapping
    public Result<ExpenseDTO> updateExpense(@RequestBody ExpenseDTO expenseDTO) {
        UUID userId = UserContext.getCurrentUserId();
        ExpenseDTO updatedExpense = expenseService.updateExpense(userId, expenseDTO);
        return Result.success(updatedExpense);
    }
    
    /**
     * 删除费用记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteExpense(@PathVariable("id") UUID id) {
        UUID userId = UserContext.getCurrentUserId();
        expenseService.deleteExpense(userId, id);
        return Result.success();
    }
}