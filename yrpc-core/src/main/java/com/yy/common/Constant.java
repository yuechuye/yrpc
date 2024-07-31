package com.yy.common;

/**
 * 常量类，用于定义系统运行过程中的常量值。
 */
public class Constant {

    /**
     * 默认的ZooKeeper连接地址，用于RPC服务的注册与发现。
     */
    //默认zk连接ip和端口
    public static final String DEFAULT_ZK_CONNECT = "127.0.0.1:2181";

    /**
     * 服务的默认端口
     */
    public static final int DEFAULT_SERVICE_PORT = 8094;

    /**
     * 默认的ZooKeeper会话超时时间，单位为毫秒。
     */
    //默认zk超时时间
    public static final int DEFAULT_ZK_TIMEOUT = 10000;

    /**
     * RPC服务提供者的根路径，在ZooKeeper中用于存放服务提供者的相关信息。
     */
    //生产者根路径
    public static final String BASE_PROVIDER_PATH = "/yrpc/provider";

    /**
     * RPC服务消费者根路径，在ZooKeeper中用于存放服务消费者的相关信息。
     */
    //消费者根路径
    public static final String BASE_CONSUMER_PATH = "/yrpc/consumer";
}
