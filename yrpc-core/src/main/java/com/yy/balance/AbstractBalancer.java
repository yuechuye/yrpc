package com.yy.balance;

import com.yy.RpcBootStrap;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象均衡器类，为实现服务选择提供基础框架。
 * 该类为抽象类，具体的服务选择策略由子类实现。
 */
public abstract class AbstractBalancer implements Balancer{

    /**
     * 服务选择器缓存，用于存储每个服务名称对应的選擇器。
     * 使用ConcurrentHashMap以保证线程安全。
     */
    Map<String,Selector> cache = new ConcurrentHashMap<>(8);

    /**
     * 根据服务名称和组别选择一个服务地址。
     * 如果缓存中不存在对应的服务选择器，则根据服务名称和组别从注册中心获取服务列表，
     * 并创建一个新的选择器，缓存起来供后续使用。
     *
     * @param serviceName 服务名称
     * @param group 服务组别
     * @return 返回选择的服务器地址
     */
    @Override
    public InetSocketAddress selectServiceAddress(String serviceName, String group) {
        Selector selector = cache.get(serviceName);
        if (selector == null){
            List<InetSocketAddress> serviceList =
                    RpcBootStrap.getInstance()
                            .getConfiguration().getRegistryConfig().getRegistry().lookUp(serviceName,group);
            selector = getSelector(serviceList);
            cache.put(serviceName,selector);
        }

        return selector.getNext();
    }

    /**
     * 根据服务列表创建并返回一个选择器。
     * 该方法由子类实现，提供具体的选路策略。
     *
     * @param serviceList 服务地址列表
     * @return 返回创建的选择器
     */
    protected abstract Selector getSelector(List<InetSocketAddress> serviceList);

    /**
     * 重新加载平衡配置，用于更新服务选择器。
     * 当服务列表发生变化时，通过该方法更新缓存中的选择器，以应用新的负载均衡策略。
     *
     * @param serviceName 服务名称
     * @param serviceList 新的服务地址列表
     */
    @Override
    public void reLoadBalance(String serviceName, List<InetSocketAddress> serviceList) {
        //根据新的服务去获取新的selector
        cache.put(serviceName,getSelector(serviceList));
    }
}

