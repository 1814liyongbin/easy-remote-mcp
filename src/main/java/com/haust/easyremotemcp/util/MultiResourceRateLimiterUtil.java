package com.haust.easyremotemcp.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: liyongbin
 * @date: 2025/4/21 18:18
 * @description: 简单限流工具 重启后重置
 */
@Component
public class MultiResourceRateLimiterUtil {
    // 每日最大请求数
    private static final int DAILY_LIMIT = 500;

    // 限流器存储（线程安全）
    private final Map<String, Limiter> limiters = new ConcurrentHashMap<>();

    /**
     * 尝试获取指定资源的请求许可
     * @param resource 资源标识（如接口路径）
     * @return true-获取成功，false-达到限制
     */
    public boolean tryAcquire(String resource) {
        return getLimiter(resource).tryAcquire();
    }

    /**
     * 获取指定资源的剩余可用次数
     */
    public int remaining(String resource) {
        return getLimiter(resource).remaining();
    }

    /**
     * 获取或创建指定资源的限流器
     */
    private Limiter getLimiter(String resource) {
        return limiters.computeIfAbsent(resource, k -> new Limiter());
    }

    /**
     * 内部限流器实例
     */
    private static class Limiter {
        private final AtomicInteger counter = new AtomicInteger(0);
        private volatile LocalDate currentDate = LocalDate.now();

        synchronized boolean tryAcquire() {
            checkDateReset();
            return counter.getAndIncrement() < DAILY_LIMIT;
        }

        int remaining() {
            checkDateReset();
            return Math.max(DAILY_LIMIT - counter.get(), 0);
        }

        private synchronized void checkDateReset() {
            LocalDate today = LocalDate.now();
            if (!today.equals(currentDate)) {
                counter.set(0);
                currentDate = today;
            }
        }
    }
}
