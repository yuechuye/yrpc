package com.yy.discovery;


import com.yy.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 提炼模板方法
 */
/**
 * 抽象注册中心类，实现了Registry接口。
 * 该类为注册中心的操作提供了一个抽象的实现，包括服务的注册和查询。
 * 具体的注册中心实现类可以通过继承此类，并重写其方法来实现特定注册中心的功能。
 */
public class AbstractRegistry implements Registry{
    /**
     * 注册服务。
     * 该方法用于将指定的服务配置注册到注册中心。
     * @param serviceConfig 服务的配置信息，包含了服务的名称、地址等信息。
     */
    @Override
    public void registry(ServiceConfig<?> serviceConfig) {
        // 具体的注册逻辑在这里实现
    }

    /**
     * 查询服务的地址。
     * 通过服务名称和组别查询注册中心中记录的服务提供者的地址信息。
     * @param name 服务的名称，用于唯一标识一个服务。
     * @param group 服务所属的组别，用于对服务进行分类。
     * @return 返回一个包含服务提供者地址的列表。如果找不到相应的服务提供者，则返回null。
     */
    @Override
    public List<InetSocketAddress> lookUp(String name, String group) {
        // 具体的查询逻辑在这里实现
        return null;
    }


}
