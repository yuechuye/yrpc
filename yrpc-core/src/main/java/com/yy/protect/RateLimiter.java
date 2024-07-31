package com.yy.protect;

/**
 * 限流器接口。
 * 该接口定义了限流逻辑的方法，用于控制是否允许进行请求。
 * 限流器可以在高并发场景下保护系统，通过限制请求的速率来避免系统过载。
 */
public interface RateLimiter {

    /**
     * 检查是否允许当前请求通过。
     * <p>
     * 该方法用于在执行实际请求之前，判断是否满足限流条件。
     * 如果满足条件（即允许通过），则返回true；否则返回false，阻止请求继续。
     * </p>
     *
     * @return 如果允许请求通过，则返回true；否则返回false。
     */
    /**
     * 是否允许请求放行
     * @return true允许，false拒绝
     */
    boolean allowRequest();
}
