package com.yy;


import com.yy.balance.Balancer;
import com.yy.balance.RoundRobinBalancer;
import com.yy.common.IdGenerator;
import com.yy.discovery.RegistryConfig;
import com.yy.protect.CircuitBreaker;
import com.yy.protect.RateLimiter;
import lombok.Data;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yy.common.Constant.DEFAULT_SERVICE_PORT;
import static com.yy.common.Constant.DEFAULT_ZK_CONNECT;

/**
 * 全局配置类
 */
/**
 * 应用的全局配置类，集中管理各种配置信息。
 */
@Data
public class Configuration {
    /**
     * 服务监听的端口号，默认为8094。
     */
    private int port = DEFAULT_SERVICE_PORT;

    /**
     * 应用程序的名称，默认为"default"。
     */
    // 配置信息-->应用程序的名字
    private String appName = "default";

    /**
     * 服务所属的分组，默认为"default"。
     */
    // 分组信息
    private String group = "default";

    /**
     * 注册中心的配置，默认使用ZooKeeper作为注册中心，地址为127.0.0.1:2181。
     */
    // 配置信息-->注册中心
    private RegistryConfig registryConfig = new RegistryConfig("zookeeper://" + DEFAULT_ZK_CONNECT);

    /**
     * 序列化协议的类型，默认使用jdk序列化。
     */
    // 配置信息-->序列化协议
    private String serializeType = "jdk";

    /**
     * 数据压缩的类型，默认使用gzip压缩。
     */
    // 配置信息-->压缩使用的协议
    private String compressType = "gzip";

    /**
     * 全局唯一ID生成器，用于生成业务ID。
     */
    // 配置信息-->id发射器
    public IdGenerator idGenerator = new IdGenerator(1, 2);

    /**
     * 负载均衡器，用于客户端调用服务时的负载均衡，默认使用轮询算法。
     */
    // 配置信息-->负载均衡策略
    private Balancer balancer = new RoundRobinBalancer();

    /**
     * 为每个IP地址配置一个速率限制器，用于限制来自特定IP的请求速率。
     */
    // 为每一个ip配置一个限流器
    private final Map<SocketAddress, RateLimiter> everyIpRateLimiter = new ConcurrentHashMap<>(16);

    /**
     * 为每个IP地址配置一个断路器，用于实现服务的熔断保护。
     */
    // 为每一个ip配置一个断路器，熔断
    private final Map<SocketAddress, CircuitBreaker> everyIpCircuitBreaker = new ConcurrentHashMap<>(16);
}
