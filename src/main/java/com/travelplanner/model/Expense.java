package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * 费用记录实体类
 */
@Data
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;         // 费用记录ID

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private TravelPlan travelPlan;  // 关联的旅行计划

    @Column(name = "expense_type", length = 50, nullable = false)
    private String expenseType;  // 费用类型

    @Column(name = "description", length = 200)
    private String description;  // 描述

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;  // 金额

    @Column(name = "currency", length = 10, nullable = false)
    private String currency = "CNY";  // 货币类型

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;  // 支付方式

    @Column(name = "receipt_url", length = 255)
    private String receiptUrl;  // 发票URL

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;   // 创建时间
}