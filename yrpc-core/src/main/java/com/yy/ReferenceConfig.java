package com.yy;


import com.yy.discovery.Registry;
import com.yy.proxy.handler.ConsumerInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * 该类提供了对服务引用的配置。
 * 通过该配置，可以生成一个代理对象，用于消费远程服务。
 *
 * @param <T> 服务接口类型
 */
@Slf4j
public class ReferenceConfig<T> {

    /**
     * 服务接口类
     */
    private Class<T> interfaceRef;

    public Class<T> getInterfaceRef() {
        return interfaceRef;
    }

    /**
     * 注册中心配置
     */
    private Registry registry;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 获取服务代理对象。
     * 该方法通过Java的动态代理机制，创建一个实现了服务接口的代理对象。
     * 代理对象在调用方法时，会通过InvocationHandler转发调用到远程服务。
     *
     * @return 服务代理对象
     */
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T>[] classes = new Class[]{interfaceRef};
        // 创建代理实例，处理客户端调用，实现远程服务调用
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes,
                new ConsumerInvocationHandler(registry, interfaceRef, RpcBootStrap.getInstance()
                        .getConfiguration().getGroup()));
        return (T) helloProxy;
    }
}
