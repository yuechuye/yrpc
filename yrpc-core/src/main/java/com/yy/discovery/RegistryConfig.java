package com.yy.discovery;


import com.yy.common.Constant;
import com.yy.common.exceptions.RegistryException;
import com.yy.discovery.impl.ZookeeperRegistry;

/**
 * 注册中心配置类，用于根据连接字符串创建并返回相应的注册中心实例。
 */
public class RegistryConfig {

    // 注册中心的连接字符串
    private final String connectString;

    /**
     * 构造函数，初始化注册中心的连接字符串。
     *
     * @param connectString 注册中心的连接字符串，格式为"协议://主机:端口"。
     */
    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 根据连接字符串创建并返回相应的注册中心实例。
     *
     * @return 注册中心的实例。
     * @throws RegistryException 如果无法确定注册中心类型，则抛出注册中心异常。
     */
    /**
     * 返回注册中心实例
     * @return 注册中心实例
     */
    public Registry getRegistry() {
        // 从连接字符串中获取注册中心的类型
        String registryType = getRegistryType(connectString,true).toLowerCase().trim();
        // 如果注册中心类型为zookeeper，则创建并返回ZookeeperRegistry实例
        if (registryType.equals("zookeeper")){
            String host = getRegistryType(connectString, false);
            return new ZookeeperRegistry(host, Constant.DEFAULT_ZK_TIMEOUT);
        }
        // 如果无法确定注册中心类型，则抛出异常
        throw new RegistryException("没有拿到注册中心");
    }

    /**
     * 从连接字符串中获取注册中心的类型或主机和端口信息。
     *
     * @param connectString 注册中心的连接字符串。
     * @param ifType 是否需要返回注册中心的类型，如果为true，则返回类型；如果为false，则返回主机和端口信息。
     * @return 注册中心的类型或主机和端口信息。
     * @throws RuntimeException 如果连接字符串的格式不正确，则抛出运行时异常。
     */
    /**
     * 获取注册中心种类
     * @param connectString 连接字符串
     * @param ifType 是否查找连接种类
     * @return 注册中心种类或者ip地址和端口号
     */
    public String getRegistryType(String connectString, boolean ifType){
        // 根据"://"分割连接字符串，获取协议和主机端口信息
        String[] typeAndHost = connectString.split("://");
        // 检查分割结果是否包含两部分，如果不包含，则连接字符串格式不正确，抛出异常
        if (typeAndHost.length != 2){
            throw new RuntimeException("给定的注册中心连接url不合法");
        }
        // 根据ifType的值决定返回注册中心类型还是主机端口信息
        if (ifType){
            return typeAndHost[0];
        } else {
            return typeAndHost[1];
        }
    }
}
