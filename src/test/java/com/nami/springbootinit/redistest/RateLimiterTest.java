package com.nami.springbootinit.redistest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.nami.springbootinit.manager.RedisLimiterManager;

import javax.annotation.Resource;

/**
 * @author nami404
 * * @date 2025/3/6 22:42
 */
@SpringBootTest
public class RateLimiterTest {
    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    public void testRateLimiter() {
        String key = "testLimiter-" + "111122223333";
        redisLimiterManager.doRateLimit(key);
        redisLimiterManager.doRateLimit(key);
        //redisLimiterManager.doRateLimit(key); // 限流一个key最多只能访问两次，第三次就会抛出 太多请求异常
    }

}
