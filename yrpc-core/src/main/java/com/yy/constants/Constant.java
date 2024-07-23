package com.yy.constants;

/**
 * 常量类，用于存储系统运行过程中的常量值。
 * @author yuechu
 */
//todo 后续此类中的常量应动态配置
public class Constant {

    /**
     * 默认的ZooKeeper连接地址。
     * 该地址指定了ZooKeeper服务的主机和端口，用于建立与ZooKeeper的连接。
     * 默认值为"127.0.0.1:2181"。
     */
    //默认zk连接ip和端口
    public static final String DEFAULT_ZK_CONNECT = "127.0.0.1:2181";

    /**
     * 默认的ZooKeeper会话超时时间。
     * 该值指定了ZooKeeper会话的超时时间，用于控制客户端与ZooKeeper服务之间的连接状态。
     * 如果在指定的时间内没有与ZooKeeper服务进行交互，连接将被断开。
     * 默认值为10000毫秒。
     */
    //默认zk超时时间
    public static final int DEFAULT_ZK_TIMEOUT = 10000;

    /**
     * 生产者根路径。
     * 在ZooKeeper中，生产者的路径用于注册和管理生产者服务。
     */
    //生产者根路径
    public static final String BASE_PROVIDER_PATH = "/rpc-yy/provider";

    /**
     * 消费者根路径。
     * 在ZooKeeper中，消费者的路径用于注册和管理消费者服务。
     */
    //消费者根路径
    public static final String BASE_CONSUMER_PATH = "/rpc-yy/consumer";
}
