package com.travelplanner.repository;

import com.travelplanner.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 费用记录仓库接口
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    
    /**
     * 根据旅行计划ID查询费用记录列表
     * @param planId 旅行计划ID
     * @return 费用记录列表
     */
    List<Expense> findByTravelPlanId(UUID planId);
    
    /**
     * 根据旅行计划ID和费用类型查询费用记录
     * @param planId 旅行计划ID
     * @param expenseType 费用类型
     * @return 费用记录列表
     */
    List<Expense> findByTravelPlanIdAndExpenseType(UUID planId, String expenseType);
    
    /**
     * 根据旅行计划ID删除所有相关费用记录
     * @param planId 旅行计划ID
     */
    void deleteByTravelPlanId(UUID planId);
}
