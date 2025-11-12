package com.travelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 更新旅行计划请求对象
 */
@Data
public class UpdateTravelPlanRequest {
    @NotNull(message = "计划ID不能为空")
    private UUID id;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "目的地不能为空")
    private String destination;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    private BigDecimal budget;
    private Integer peopleCount;
    private String preferences;
    private String planJson;
    private String status;
}
