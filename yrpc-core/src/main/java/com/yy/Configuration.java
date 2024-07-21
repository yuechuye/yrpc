package com.yy;


import com.yy.discover.RegistryConfig;
import lombok.Data;

/**
 * 全局配置类
 */
@Data
public class Configuration {
    // 配置信息-->端口号
    private int port = 8094;
    // 配置信息-->应用程序的名字
    private String appName = "default";
    // 分组信息
    private String group = "default";
    // 配置信息-->注册中心
    private RegistryConfig registryConfig = new
            RegistryConfig("zookeeper://43.139.111.22:2181");
    // 配置信息-->序列化协议
    private String serializeType = "jdk";
    // 配置信息-->压缩使用的协议
    private String compressType = "gzip";
    // 配置信息-->id发射器
    public IdGenerator idGenerator = new IdGenerator(1, 2);
    // 配置信息-->负载均衡策略
//    private Balancer balancer = new RoundRobinBalancer();
//
//    // 为每一个ip配置一个限流器
//    private final Map<SocketAddress, RateLimiter> everyIpRateLimiter = new ConcurrentHashMap<>(16);
//
//    // 为每一个ip配置一个断路器，熔断
//    private final Map<SocketAddress, CircuitBreaker> everyIpCircuitBreaker = new ConcurrentHashMap<>(16);
}
