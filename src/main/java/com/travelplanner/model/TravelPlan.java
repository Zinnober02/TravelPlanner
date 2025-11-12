package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 旅行计划实体类
 */
@Data
@Entity
@Table(name = "travel_plans")
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;         // 旅行计划ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;       // 用户ID，关联用户表

    @Column(name = "title", length = 100, nullable = false)
    private String title;    // 标题

    @Column(name = "destination", length = 100, nullable = false)
    private String destination;  // 目的地

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 开始日期

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;    // 结束日期

    @Column(name = "budget", precision = 10, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;  // 预算

    @Column(name = "people_count")
    private Integer peopleCount = 1;  // 人数

    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences;  // 偏好设置

    @Column(name = "plan_json", columnDefinition = "TEXT")
    private String planJson;     // 规划JSON数据

    @Column(name = "status", length = 20)
    private String status = "draft";  // 状态

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;   // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;   // 更新时间
}