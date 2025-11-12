package com.travelplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;         // 用户ID
    
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;   // 用户名
    
    @JsonIgnore               // 密码不返回给前端
    @Column(name = "password", length = 255, nullable = false)
    private String password;   // 密码（加密存储）
    
    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;      // 邮箱
    
    @Column(name = "phone", length = 20)
    private String phone;      // 手机号
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;   // 创建时间
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;   // 更新时间
}