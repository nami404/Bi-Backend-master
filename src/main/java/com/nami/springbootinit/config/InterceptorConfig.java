package com.nami.springbootinit.config;

import com.nami.springbootinit.aop.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * author Nami
 * date 2025/9/18 10:53
 * description 专门处理拦截器的配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                // 需要拦截的接口（所有接口都拦截，除了下面放行的）
                .addPathPatterns("/**")
                // 放行的接口（登录、注册、Swagger文档等）
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/userLogin",
                        "/user/getUserByUserId",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/favicon.ico",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );
    }
}
