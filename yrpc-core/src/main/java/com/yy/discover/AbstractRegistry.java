/**
 * 抽象注册中心类，作为注册中心的基类，实现了Registry接口。
 * 提供了注册服务和查询服务的基本框架，具体的注册与查询逻辑由子类实现。
 */
package com.yy.discover;

import com.yy.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 提炼模板方法
 */
public class AbstractRegistry implements Registry{
    /**
     * 注册服务。
     * 根据ServiceConfig对象中的信息，将服务注册到注册中心。
     * 具体的注册逻辑由子类实现。
     *
     * @param serviceConfig 服务配置对象，包含服务的详细信息。
     */
    @Override
    public void registry(ServiceConfig<?> serviceConfig) {

    }

    /**
     * 查询服务。
     * 根据服务名称和组别，从注册中心查询服务的提供者地址列表。
     * 具体的查询逻辑由子类实现。
     *
     * @param name 服务名称。
     * @param group 服务组别。
     * @return 服务提供者地址列表，如果找不到则返回空列表。
     */
    @Override
    public List<InetSocketAddress> lookUp(String name, String group) {
        return null;
    }


}
