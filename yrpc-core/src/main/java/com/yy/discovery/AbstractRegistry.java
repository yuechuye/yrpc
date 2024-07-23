/**
 * 注册中心的抽象类，实现了Registry接口。
 * 提供了注册服务和查询服务的基本框架，具体的注册与查询逻辑由子类实现。
 */
package com.yy.discovery;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 提炼模板方法
 * @author yuechu
 */
public class AbstractRegistry implements Registry{
    /**
     * 注册服务。
     * 根据ServiceConfig对象中的信息，将服务注册到注册中心。
     * 具体的注册逻辑由子类实现。
     *
     * @param serviceConfig 服务的配置信息，包含服务名、组别等注册所需信息。
     */
    @Override
    public void registry(ServiceConfig<?> serviceConfig) {

    }

    /**
     * 查询服务。
     * 根据服务名和组别，从注册中心查询服务的提供者地址列表。
     * 具体的查询逻辑由子类实现。
     *
     * @param name 服务名，用于查询服务的标识。
     * @param group 服务的组别，用于对服务进行分类。
     * @return 服务提供者的地址列表，返回null表示查询失败。
     */
    @Override
    public List<InetSocketAddress> lookUp(String name, String group) {
        return null;
    }


}
