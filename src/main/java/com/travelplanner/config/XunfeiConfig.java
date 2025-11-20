package com.travelplanner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 讯飞API配置
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "xunfei")
public class XunfeiConfig {
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * API密钥
     */
    private String apiSecret;
    
    /**
     * API主机地址
     */
    private String host;
}