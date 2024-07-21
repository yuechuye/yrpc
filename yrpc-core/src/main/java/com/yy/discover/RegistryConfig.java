package com.yy.discover;

import com.yy.Constant;
import com.yy.discover.impl.ZookeeperRegistry;
import com.yy.exception.RegistryException;

/**
 * 注册中心配置类，用于根据连接字符串创建相应的注册中心实例。
 *
 * @author yuechu
 */
public class RegistryConfig {

    // 注册中心的连接字符串
    private final String connectString;

    /**
     * 构造函数，初始化注册中心的连接字符串。
     *
     * @param connectString 注册中心的连接字符串，格式为协议://主机:端口
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
        // 解析注册中心类型和主机信息
        //todo 目前仅支持 zookeeper ，后续添加优化为策略
        String registryType = getRegistryType(connectString, true).toLowerCase().trim();
        if ("zookeeper".equals(registryType)) {
            String host = getRegistryType(connectString, false);
            // 返回ZooKeeper注册中心实例
            return new ZookeeperRegistry(host, Constant.DEFAULT_ZK_TIMEOUT);
        }
        throw new RegistryException("没有拿到注册中心");
    }


    /**
     * 解析连接字符串，获取注册中心的类型或主机信息。
     *
     * @param connectString 连接字符串
     * @param ifType        是否需要返回类型，true表示返回类型，false表示返回主机信息
     * @return 注册中心的类型或主机信息
     * @throws RuntimeException 如果连接字符串格式不正确，则抛出运行时异常。
     */
    public String getRegistryType(String connectString, boolean ifType) {
        String[] typeAndHost = connectString.split("://");
        if (typeAndHost.length != 2) {
            throw new RuntimeException("给定的注册中心连接url不合法");
        }
        if (ifType) {
            return typeAndHost[0];
        } else {
            return typeAndHost[1];
        }
    }
}
