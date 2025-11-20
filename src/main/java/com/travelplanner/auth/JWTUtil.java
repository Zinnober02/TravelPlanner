package com.travelplanner.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类
 * 用于生成和验证JWT token
 */
@Component
public class JWTUtil {

    @Value("${jwt.secret:travelplanner-secret-key-for-jwt-token-generation}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")  // 默认24小时
    private long jwtExpiration;

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT token字符串
     */
    public String generateToken(UUID userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中提取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * 从token中提取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    /**
     * 验证token是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证token是否有效（别名方法）
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }

    /**
     * 获取token过期时间
     * @param token JWT token
     * @return 过期时间
     */
    public long getExpirationTime(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().getTime();
    }

    /**
     * 从token中提取所有claims
     * @param token JWT token
     * @return Claims对象
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取签名密钥
     * @return Key对象
     */
    private Key getSignInKey() {
        // 使用BASE64URL而不是BASE64，因为JWT使用的是base64url编码格式
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}