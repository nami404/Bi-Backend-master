package com.nami.springbootinit.proxytest2;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author nami404
 * * @date 2025/3/6 01:28
 */
@Service
public class PageAccessCounterService {
    @Resource
    private RedissonClient redissonClient;

    private static final String LOCK_KEY = "page_access_lock";

    private static final String PAGE_ACCESS_COUNT_LOCK = "page_access_count";

    public long incrementPageAccess() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            boolean tryLock = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (tryLock) {
                RBucket<Long> bucket = redissonClient.getBucket(PAGE_ACCESS_COUNT_LOCK);
                long currentValue = 0;

                Long value = bucket.get();
                if (value != null) {
                    currentValue = value;
                }

                Long newCount = currentValue + 1;
                bucket.set(newCount);   // 页面访问+1
                return newCount;
            } else {
                return -1;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public long getPageAccessCount() {
        RBucket<Long> bucket = redissonClient.getBucket(PAGE_ACCESS_COUNT_LOCK);
        Long value = bucket.get();
        if (value != null) {
            return value;
        }
        return 0L;
    }
}
