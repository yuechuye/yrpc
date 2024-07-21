package com.yy.discover.impl;

import com.yy.Constant;

import com.yy.ServiceConfig;
import com.yy.discover.AbstractRegistry;
import com.yy.exception.DiscoveryException;
import com.yy.utils.NetUtils;
import com.yy.utils.zookeeper.ZookeeperNode;
import com.yy.utils.zookeeper.ZookeeperUtils;
import com.yy.watcher.UpAndDownWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于ZooKeeper实现的服务注册与发现。
 * 该类继承自AbstractRegistry，实现了服务的注册与查找功能。
 * @author yuechu
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    // ZooKeeper客户端实例
    private final ZooKeeper zooKeeper;

    /**
     * 默认构造函数，初始化ZooKeeper客户端。
     */
    public ZookeeperRegistry() {
        this.zooKeeper = ZookeeperUtils.zooKeeperCreate();
    }

    /**
     * 带有连接字符串和超时时间的构造函数。
     *
     * @param connectString ZooKeeper的连接字符串。
     * @param timeout 连接超时时间。
     */
    public ZookeeperRegistry(String connectString, int timeout){
        this.zooKeeper = ZookeeperUtils.zookeeperCreate(connectString,timeout);
    }

    /**
     * 注册服务到ZooKeeper。
     *
     * @param service 需要注册的服务配置。
     */
    @Override
    public void registry(ServiceConfig<?> service) {
        // 构建服务的注册路径
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + service.getInterfaceProvider().getName();
        // 创建服务的目录节点
        // 创建服务的持久节点
        servicePath = servicePath + "/" + service.getGroup();
        ZookeeperNode serviceNode = new ZookeeperNode(servicePath, null);
        ZookeeperUtils.CreateNode(zooKeeper,serviceNode,null, CreateMode.PERSISTENT);
        // 创建服务提供者的临时节点
        // 创建服务保存的临时节点，ip:port
        String ip = NetUtils.getInIp();
        String path = servicePath + "/" + ip + ":" + 8094;
        ZookeeperNode node = new ZookeeperNode(path,null);
        ZookeeperUtils.CreateNode(zooKeeper,node,null,CreateMode.EPHEMERAL);
        log.debug("服务{}已经被注册",service.getInterfaceProvider().getName());
    }

    /**
     * 通过服务名和组名查找服务提供者的地址。
     *
     * @param serviceName 服务名。
     * @param group 服务组名。
     * @return 服务提供者的地址列表。
     * @throws DiscoveryException 如果没有找到服务，则抛出异常。
     */
    @Override
    public List<InetSocketAddress> lookUp(String serviceName, String group) {
        // 构建服务查找路径
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + serviceName + "/" + group;
        // 获取服务提供者的子节点列表，并注册上线监听器
        List<String> children =
                ZookeeperUtils.getChildren(zooKeeper, servicePath, new UpAndDownWatcher());
        List<InetSocketAddress> inetSocketAddresses = new ArrayList<>();
        // 解析子节点名称（IP:PORT）为InetSocketAddress对象
        for (String child : children) {
            String[] ipAndPort = child.split(":");
            inetSocketAddresses.add(new InetSocketAddress(ipAndPort[0],Integer.parseInt(ipAndPort[1])));
        }
        // 如果没有找到服务提供者，则抛出异常
        if (inetSocketAddresses.isEmpty()){
            throw new DiscoveryException("没有发现可用服务");
        }
        log.debug("消费者发现节点【{}】的可用服务{}", servicePath, inetSocketAddresses.get(0));
        return inetSocketAddresses;
    }
}
