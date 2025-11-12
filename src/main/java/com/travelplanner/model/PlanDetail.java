package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.UUID;

/**
 * 行程详情实体类
 */
@Data
@Entity
@Table(name = "plan_details")
public class PlanDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;         // 行程详情ID

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private TravelPlan travelPlan;  // 关联的旅行计划

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;  // 第几天

    @Column(name = "activity_type", length = 50, nullable = false)
    private String activityType;  // 活动类型

    @Column(name = "title", length = 100, nullable = false)
    private String title;    // 标题

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;  // 描述

    @Column(name = "location", length = 255)
    private String location;  // 地点

    @Column(name = "start_time")
    private LocalTime startTime;  // 开始时间

    @Column(name = "end_time")
    private LocalTime endTime;    // 结束时间

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;  // 费用

    @Column(name = "image_url", length = 255)
    private String imageUrl;  // 图片URL

    @Column(name = "sort_order")
    private Integer sortOrder = 0;  // 排序顺序

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;   // 创建时间
}