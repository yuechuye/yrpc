package com.yy.discovery.impl;


import com.yy.RpcBootStrap;
import com.yy.ServiceConfig;
import com.yy.common.Constant;
import com.yy.common.exceptions.DiscoveryException;
import com.yy.common.utils.NetUtils;
import com.yy.common.utils.zookeeper.ZookeeperNode;
import com.yy.common.utils.zookeeper.ZookeeperUtils;
import com.yy.discovery.AbstractRegistry;
import com.yy.watcher.UpAndDownWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * 基于ZooKeeper实现的服务注册与发现中心。
 * 该类继承自AbstractRegistry，实现了服务的注册与查找功能。
 */
public class ZookeeperRegistry extends AbstractRegistry {

    // ZooKeeper实例
    private ZooKeeper zooKeeper;

    /**
     * 默认构造函数，初始化ZooKeeper实例。
     */
    public ZookeeperRegistry() {
        this.zooKeeper = ZookeeperUtils.zooKeeperCreate();
    }

    /**
     * 带参数的构造函数，通过传入连接字符串和超时时间初始化ZooKeeper实例。
     *
     * @param connectString ZooKeeper的连接字符串
     * @param timeout 连接超时时间
     */
    public ZookeeperRegistry(String connectString, int timeout){
        this.zooKeeper = ZookeeperUtils.zookeeperCreate(connectString,timeout);
    }

    /**
     * 注册服务到ZooKeeper。
     * 在ZooKeeper中创建服务的永久节点和提供者地址的临时节点。
     *
     * @param service 待注册的服务配置信息
     */
    @Override
    public void registry(ServiceConfig<?> service) {
        // 构建服务注册路径
        // 构建服务在注册中心中的路径，基础路径 + 接口提供者的类名
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + service.getInterfaceProvider().getName();

        // 添加分组信息到路径
        // 在服务路径下添加分组节点
        servicePath = servicePath + "/" + service.getGroup();

        // 创建服务节点
        // 创建表示服务持久节点的 ZookeeperNode 对象
        ZookeeperNode serviceNode = new ZookeeperNode(servicePath, null);

        // 在ZooKeeper中创建服务节点
        // 在 Zookeeper 中创建服务持久节点
        ZookeeperUtils.CreateNode(zooKeeper, serviceNode, null, CreateMode.PERSISTENT);

        // 获取当前机器IP
        // 获取当前服务所在主机的 IP 地址
        String ip = NetUtils.getInIp();

        // 构建提供者地址节点路径
        // 构建服务在注册中心中的临时节点路径，包括 IP 地址和端口号
        String path = servicePath + "/" + ip + ":" + RpcBootStrap.getInstance().getConfiguration().getPort();

        // 创建提供者地址节点
        // 创建表示服务临时节点的 ZookeeperNode 对象
        ZookeeperNode node = new ZookeeperNode(path, null);

        // 在ZooKeeper中创建提供者地址节点
        // 在 Zookeeper 中创建服务临时节点
        ZookeeperUtils.CreateNode(zooKeeper, node, null, CreateMode.EPHEMERAL);

        // 打印注册信息
        // 打印调试信息，表示服务已成功注册
        log.debug("服务{}已经被注册", service.getInterfaceProvider().getName());
    }

    /**
     * 从ZooKeeper查找服务提供者的地址。
     * 根据服务名和分组查询ZooKeeper中的临时节点，获取提供者地址列表。
     *
     * @param serviceName 服务名称
     * @param group 服务分组
     * @return 提供者地址列表
     */
    @Override
    public List<InetSocketAddress> lookUp(String serviceName, String group) {
        // 构建服务查询路径
        // 构建服务在注册中心中的路径，包括基础路径、服务名称和分组
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + serviceName + "/" + group;

        // 获取服务提供者的子节点列表
        // 获取指定路径下的所有子节点，即服务实例的地址列表
        List<String> children = ZookeeperUtils.getChildren(zooKeeper, servicePath, new UpAndDownWatcher());

        // 初始化地址列表
        // 准备用于存储服务地址的列表
        List<InetSocketAddress> inetSocketAddresses = new ArrayList<>();

        // 遍历子节点，解析地址信息
        // 遍历每个子节点，解析出 IP 地址和端口号，并添加到地址列表中
        for (String child : children) {
            String[] ipAndPort = child.split(":");
            inetSocketAddresses.add(new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
        }

        // 检查地址列表是否为空，为空则抛出异常
        // 如果未发现可用服务地址，抛出发现异常
        if (inetSocketAddresses.isEmpty()) {
            throw new DiscoveryException("没有发现可用服务");
        }

        // 打印查找信息
        // 打印调试信息，显示消费者发现的可用服务地址
        log.debug("消费者发现节点【{}】的可用服务{}", servicePath, inetSocketAddresses.get(0));

        // 返回地址列表
        // 返回解析后的服务地址列表
        return inetSocketAddresses;
    }

}
