package com.yy;

/**
 * @author yuechuye
 */
public class Constant {

    //默认zk连接ip和端口
    public static final String DEFAULT_ZK_CONNECT = "43.139.111.22:2181";

    //默认zk超时时间
    public static final int DEFAULT_ZK_TIMEOUT = 10000;

    //生产者根路径
    public static final String BASE_PROVIDER_PATH = "/rpc-yy/provider";

    //消费者根路径
    public static final String BASE_CONSUMER_PATH = "/rpc-yy/consumer";
}
