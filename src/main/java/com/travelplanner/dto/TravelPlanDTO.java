package com.travelplanner.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 旅行计划数据传输对象
 */
@Data
public class TravelPlanDTO {
    private UUID id;
    private String title;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private Integer peopleCount;
    private String preferences;
    private String planJson;
    private String status;
    private Long days; // 计算的行程天数
    private String createdAt; // 格式化的创建时间
}
