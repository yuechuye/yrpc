package com.yy.discovery;

import com.yy.discovery.impl.ZookeeperRegistry;

/**
 * 注册中心配置类，用于根据连接字符串创建相应的注册中心实例。
 *
 * @author yuechu
 */
public class RegistryConfig {

    /**
     * 注册中心的连接字符串。
     */
    private final String connectString;

    /**
     * 构造函数，初始化注册中心的连接字符串。
     *
     * @param connectString 注册中心的连接字符串。
     */
    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 返回注册中心实例
     *
     * @return 注册中心实例
     */
    public Registry getRegistry() {
        // 获取注册中心类型
        String registryType = getRegistryType(connectString).toLowerCase().trim();
        // 如果是ZooKeeper类型，则创建并返回ZooKeeper注册中心实例
        //todo 后续有不同的注册中心类型，需要修改为工厂
        if ("zookeeper".equals(registryType)) {
            return new ZookeeperRegistry();
        }
        // 如果不是支持的注册中心类型，则抛出异常
        throw new RuntimeException("没有拿到注册中心");
    }

    /**
     * 获取注册中心种类
     *
     * @param connectString 连接字符串
     * @return 注册中心种类
     */
    public String getRegistryType(String connectString) {
        // 根据"://"分割连接字符串，获取类型和主机信息
        String[] typeAndHost = connectString.split("://");
        // 检查分割结果是否合法，如果不合法则抛出异常
        if (typeAndHost.length != 2) {
            throw new RuntimeException("给定的注册中心连接url不合法");
        }
        // 根据参数ifType决定是返回类型还是主机信息

        return typeAndHost[0];

    }
}
