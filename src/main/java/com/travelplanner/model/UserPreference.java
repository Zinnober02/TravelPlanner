package com.travelplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * 用户偏好实体类
 */
@Data
@Entity
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;         // 偏好ID

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;       // 关联的用户

    @Column(name = "favorite_destination", length = 100)
    private String favoriteDestination;  // 偏好目的地

    @Column(name = "travel_style", length = 50)
    private String travelStyle;  // 旅行风格

    @Column(name = "budget_preference", length = 20)
    private String budgetPreference;  // 预算偏好

    @Column(name = "accommodation_type", length = 50)
    private String accommodationType;  // 住宿类型

    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;  // 兴趣爱好

    @Column(name = "meal_preferences", columnDefinition = "TEXT")
    private String mealPreferences;  // 餐饮偏好

    @Column(name = "transportation_preference", length = 50)
    private String transportationPreference;  // 交通偏好

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;   // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;   // 更新时间
}