package com.travelplanner.dto;

import lombok.Data;

/**
 * 用户偏好数据传输对象
 */
@Data
public class UserPreferenceDTO {
    private String favoriteDestination;
    private String travelStyle;
    private String budgetPreference;
    private String accommodationType;
    private String interests;
    private String mealPreferences;
    private String transportationPreference;
}
