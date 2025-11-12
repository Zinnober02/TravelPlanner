package com.travelplanner.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

/**
 * 行程详情数据传输对象
 */
@Data
public class PlanDetailDTO {
    private UUID id;
    private UUID planId;
    private Integer dayNumber;
    private String activityType;
    private String title;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal cost;
    private String imageUrl;
    private Integer sortOrder;
}
