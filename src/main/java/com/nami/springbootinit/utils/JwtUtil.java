package com.nami.springbootinit.utils;

import com.alibaba.google.common.net.HttpHeaders;
import io.jsonwebtoken.*;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * author Nami
 * date 2025/9/16 22:07
 * description JWT工具类，封装JWT相关操作
 */
@Component
public class JwtUtil {

    private static final long EXPIRE_TIME = 15*60*1000;
    private static final String SECRET_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2";  //密钥盐

    /**
     * 将字符串密钥转换为SecretKey对象（核心替代方案）
     * 自动验证密钥长度是否符合算法要求（如HS256需256位）
     */
    private static SecretKey getSigningKey() {
        // 将字符串转换为字节数组（使用UTF-8编码，确保长度正确）
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        // 生成符合HS256算法的SecretKey（自动校验长度，不足会抛异常）
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌（包含Bearer前缀）
     * @param id 用户ID
     * @param userAccount 用户名
     * @return 格式为 "Bearer <token>" 的令牌字符串
     */
    public static String generateTokenWithBearerPrefix(Long id, String userAccount) {
        String token = generateToken(id, userAccount);
        return "Bearer " + token; // 添加Bearer前缀
    }

    /**
     * 生成原始JWT令牌（不含Bearer前缀）
     */
    private static String generateToken(Long id, String userAccount) {
        // 自定义载荷（存储非敏感信息）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("userAccount", userAccount);

        return Jwts.builder()
                .setClaims(claims) // 设置自定义载荷
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 过期时间
                .signWith(getSigningKey()) // 签名算法和密钥
                .compact();
    }

    /**
     * 从请求头中提取JWT令牌（自动处理Bearer前缀）
     * @param request HTTP请求对象
     * @return 纯令牌字符串（不含Bearer前缀），提取失败返回null
     */
    public static String extractTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头获取值
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 校验格式：不为null且以"Bearer "开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 截取前缀后的令牌部分（"Bearer "长度为7）
            return authHeader.substring(7).trim();
        }
        return null;
    }

    /**
     * 验证令牌是否有效
     * @param token 纯令牌字符串（不含Bearer前缀）
     * @return 有效返回true，否则返回false
     */
    public static boolean validateToken(String token) {
        try {
            // 解析令牌并验证签名
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return !isTokenExpired(token); // 同时检查是否过期
        } catch (JwtException | IllegalArgumentException e) {
            // 捕获所有JWT相关异常（签名错误、过期、格式错误等）
            return false;
        }
    }

    /**
     * 从令牌中提取用户名
     */
    public static String extractUserAccount(String token) {
        return extractClaim(token, claims -> claims.get("userAccount", String.class));
    }

    /**
     * 从令牌中提取用户ID
     */
    public static Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    /**
     * 提取令牌中的指定载荷
     */
    private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取令牌中所有载荷
     */
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * 检查令牌是否过期
     */
    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 提取令牌过期时间
     */
    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
