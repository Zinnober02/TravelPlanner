package com.travelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 生成旅行计划请求对象（用于语音输入）
 */
@Data
public class GenerateTravelPlanRequest {
    @NotBlank(message = "查询文本不能为空")
    private String query;
}
