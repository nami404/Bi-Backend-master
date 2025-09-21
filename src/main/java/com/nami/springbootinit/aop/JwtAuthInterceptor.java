package com.nami.springbootinit.aop;

import com.nami.springbootinit.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author Nami
 * date 2025/9/18 10:55
 * description Jwt认证拦截器
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头中获取令牌（通常放在Authorization头，格式：Bearer token）
        String token = JwtUtil.extractTokenFromRequest(request);

        // 2. 验证令牌有效性
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.getWriter().write("令牌无效或已过期");
            return false;
        }

        // 3. 令牌有效，可将用户信息存入请求属性（供后续接口使用）
        String userAccount = JwtUtil.extractUserAccount(token);
        Long userId = JwtUtil.extractUserId(token);
        request.setAttribute("currentUserAccount", userAccount);
        request.setAttribute("currentUserId", userId);

        // 5. 放行请求
        return true;
    }
}
