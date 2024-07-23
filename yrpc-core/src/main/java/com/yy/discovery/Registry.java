package com.yy.discovery;



import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yuechu
 * @description 注册中心统一接口，统一以后不同的注册中心实现方式
 */
public interface Registry {

    /**
     * 服务注册
     * @param serviceConfig 服务配置内容
     */
    void registry(ServiceConfig<?> serviceConfig);

    /**
     * 查询到一个可用的服务
     *
     * @param serviceName 服务名称
     * @return ip和端口
     */
    List<InetSocketAddress> lookUp(String serviceName,String group);


}
