package com.yy.protect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 令牌桶限流器实现类。
 * 该类实现了RateLimiter接口，使用令牌桶算法来限制请求的速率。
 */
public class TokenBuketRateLimiter implements RateLimiter{

    // 令牌桶中当前的令牌数量
    //令牌数
    private int tokens;

    // 令牌桶的容量
    //桶容量
    private final int capacity;

    // 每秒添加的令牌数量
    //增加令牌的速率
    private final int rate;

    // 上一次添加令牌的时间戳
    //上一次放令牌的时间
    private long lastTokenTime;

    /**
     * 构造函数初始化令牌桶。
     * @param capacity 令牌桶的容量
     * @param rate 每秒添加的令牌数量
     */
    public TokenBuketRateLimiter(int capacity, int rate) {
        this.tokens = capacity;
        this.capacity = capacity;
        this.rate = rate;
        this.lastTokenTime = System.currentTimeMillis();
    }

    /**
     * 尝试允许一个请求通过。
     * 如果令牌桶中有令牌，则消耗一个令牌并返回true，表示请求可以通过。
     * 如果没有令牌，则返回false，表示请求被限流。
     * 该方法是同步的，确保了并发环境下的线程安全。
     * @return 如果请求可以通过返回true，否则返回false。
     */
    @Override
    /**
     * 判断是否允许进行请求。
     * 该方法实现了令牌桶算法，用于限流。只有当桶中有令牌时，才允许请求通过。
     * 如果桶中没有令牌，则请求被拒绝。每次请求都会尝试获取一个令牌。
     *
     * @return 如果允许请求，则返回true；否则返回false。
     */
    public synchronized boolean allowRequest() {
        // 获取当前时间，用于计算时间间隔。
        // 获取当前时间
        //判断是否需要添加令牌
        long currentTime = System.currentTimeMillis();
        // 计算自上一次添加令牌以来的时间间隔。
        // 计算自上一次添加令牌以来的时间间隔
        long timeInterval = currentTime - lastTokenTime;
        // 如果时间间隔大于等于1秒，说明需要添加新的令牌。
        // 如果时间间隔大于等于1秒，添加新的令牌
        //如果时间大于等于速率就添加令牌
        if (timeInterval >= 1000) {
            // 计算应添加的令牌数量。
            // 计算应添加的令牌数量
            int addNewTokens = (int) (timeInterval / 1000 * rate);
            // 确保令牌数量不超过桶的容量。
            // 确保令牌数量不超过桶的容量
            tokens = Math.min(capacity, tokens + addNewTokens);
            // 更新上一次添加令牌的时间。
            lastTokenTime = currentTime;
        }
        // 如果桶中有令牌，消耗一个令牌并允许请求通过。
        // 如果令牌桶中有令牌，消耗一个令牌并允许请求通过
        if (tokens > 0) {
            tokens--;
            return true;
        }
        // 桶中没有令牌，请求被拒绝。
        // 令牌桶中没有令牌，请求被限流
        return false;
    }


    /**
     * 主函数用于测试令牌桶限流器。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建一个容量为10，每秒添加10个令牌的令牌桶限流器
        TokenBuketRateLimiter tokenBuketRateLimiter = new TokenBuketRateLimiter(10, 10);
        // 循环1000次，每次尝试发送一个请求
        for (int i = 0; i < 1000; i++) {
            try {
                // 模拟请求之间的间隔
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 尝试允许请求通过，并打印结果
            boolean b = tokenBuketRateLimiter.allowRequest();
            System.out.println(b);
        }
    }
}
