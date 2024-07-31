/**
 * 负载均衡器接口。
 * 该接口定义了如何从一组服务地址中选择一个可用的服务地址，以及如何重新加载服务地址列表。
 */
package com.yy.balance;

import java.net.InetSocketAddress;
import java.util.List;

public interface Balancer {

    /**
     * 从指定的服务组中选择一个可用的服务地址。
     *
     * @param serviceName 服务的名称，用于标识不同的服务。
     * @param group 服务所属的组，用于区分同一服务的不同组别。
     * @return 返回一个InetSocketAddress对象，包含选定服务的IP地址和端口号。
     */
    /**
     * 获取一个可用的服务
     * @param serviceName 服务名
     * @return ip地址和端口
     */
    InetSocketAddress selectServiceAddress(String serviceName, String group);

    /**
     * 重新加载指定服务的服务地址列表。
     * 此方法用于在运行时更新服务地址列表，以适应服务地址的变化。
     *
     * @param serviceName 要重新加载地址的服务的名称。
     * @param serviceList 新的服务地址列表，包含所有可用的服务地址。
     */
    void reLoadBalance(String serviceName, List<InetSocketAddress> serviceList);
}
