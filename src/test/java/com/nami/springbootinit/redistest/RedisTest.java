package com.nami.springbootinit.redistest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.nami.springbootinit.proxytest2.PageAccessCounterService;

import javax.annotation.Resource;

/**
 * @author nami404
 * * @date 2025/3/6 01:42
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private PageAccessCounterService pageAccessCounterService;
    @Test
    public void incrementPageAccess() {
        long newCount = pageAccessCounterService.incrementPageAccess();
        if (newCount >= 0) {
            System.out.println("页面访问次数已更新，当前访问次数: " + newCount);
        } else {
            System.out.println("未能获取到锁，访问次数更新失败");
        }
    }

    @Test
    public void getPageAccessCount() {
        long pageAccessCount = pageAccessCounterService.getPageAccessCount();
        System.out.println("页面访问次数为：" + pageAccessCount);
    }
}
