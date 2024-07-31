package com.yy.discovery;


import com.yy.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 注册中心接口。
 * 该接口定义了服务注册和查询服务地址的功能。
 */
public interface Registry {

    /**
     * 注册服务。
     * 将给定的服务配置注册到注册中心，以便其他服务能够发现和调用。
     *
     * @param serviceConfig 服务配置对象，包含了服务的相关信息。
     */
    void registry(ServiceConfig<?> serviceConfig);

    /**
     * 查询服务地址。
     * 根据服务名称和组别查询注册中心中记录的服务提供者的地址信息。
     *
     * @param serviceName 服务名称，用于标识要查询的服务。
     * @param group 服务所属的组别，用于进一步细化服务的分类。
     * @return 返回一个包含服务提供者地址的列表，每个地址由IP和端口组成。
     */
    List<InetSocketAddress> lookUp(String serviceName,String group);
}
