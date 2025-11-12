package com.travelplanner.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 费用记录数据传输对象
 */
@Data
public class ExpenseDTO {
    private UUID id;
    private UUID planId;
    private String expenseType;
    private String description;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String receiptUrl;
    private String createdAt;
}
