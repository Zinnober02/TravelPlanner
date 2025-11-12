package com.travelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建旅行计划请求对象
 */
@Data
public class CreateTravelPlanRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "目的地不能为空")
    private String destination;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    private BigDecimal budget = BigDecimal.ZERO;
    private Integer peopleCount = 1;
    private String preferences;
    private String planJson;
}
