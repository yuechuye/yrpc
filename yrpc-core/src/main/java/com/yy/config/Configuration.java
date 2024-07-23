package com.yy.config;


import com.yy.discovery.RegistryConfig;
import lombok.Data;

/**
 * 全局配置类
 * @author yuechu
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
            RegistryConfig("zookeeper://127.0.0.1:2181");
    // 配置信息-->序列化协议
    private String serializeType = "jdk";
    // 配置信息-->压缩使用的协议
    private String compressType = "gzip";

}
