package com.yy.discovery.impl;


import com.yy.RpcBootStrap;
import com.yy.constants.Constant;
import com.yy.discovery.AbstractRegistry;
import com.yy.discovery.ServiceConfig;
import com.yy.discovery.ZookeeperNode;
import com.yy.utils.NetUtils;
import com.yy.utils.ZookeeperUtils;
import com.yy.watcher.UpAndDownWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuechu
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private final ZooKeeper zooKeeper;

    public ZookeeperRegistry() {
        this.zooKeeper = ZookeeperUtils.zookeeperCreate();
    }

    @Override
    public void registry(ServiceConfig<?> service) {
        // 构建服务在注册中心中的路径，基础路径 + 接口提供者的类名
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + service.getInterfaceProvider().getName();

        // 在服务路径下添加分组节点
        servicePath = servicePath + "/" + service.getGroup();

        // 创建表示服务持久节点的 ZookeeperNode 对象
        ZookeeperNode serviceNode = new ZookeeperNode(servicePath, null);

        // 在 Zookeeper 中创建服务持久节点
        ZookeeperUtils.createNode(zooKeeper, serviceNode, null, CreateMode.PERSISTENT);

        // 获取当前服务所在主机的 IP 地址
        String ip = NetUtils.getInIp();

        // 构建服务在注册中心中的临时节点路径，包括 IP 地址和端口号
        String path = servicePath + "/" + ip + ":" + RpcBootStrap.getInstance().getConfiguration().getPort();

        // 创建表示服务临时节点的 ZookeeperNode 对象
        ZookeeperNode node = new ZookeeperNode(path, null);

        // 在 Zookeeper 中创建服务临时节点
        ZookeeperUtils.createNode(zooKeeper, node, null, CreateMode.EPHEMERAL);

        // 打印调试信息，表示服务已成功注册
        log.debug("服务{}已经被注册", service.getInterfaceProvider().getName());
    }


    @Override
    public List<InetSocketAddress> lookUp(String serviceName, String group) {
        // 构建服务在注册中心中的路径，包括基础路径、服务名称和分组
        String servicePath = Constant.BASE_PROVIDER_PATH + "/" + serviceName + "/" + group;

        // 获取指定路径下的所有子节点，即服务实例的地址列表
        List<String> children = ZookeeperUtils.getChildren(zooKeeper, servicePath, new UpAndDownWatcher());

        // 准备用于存储服务地址的列表
        List<InetSocketAddress> inetSocketAddresses = new ArrayList<>();

        // 遍历每个子节点，解析出 IP 地址和端口号，并添加到地址列表中
        for (String child : children) {
            String[] ipAndPort = child.split(":");
            inetSocketAddresses.add(new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
        }

        // 如果未发现可用服务地址，抛出发现异常
        if (inetSocketAddresses.isEmpty()) {
            throw new RuntimeException("没有发现可用服务");
        }

        // 打印调试信息，显示消费者发现的可用服务地址
        log.debug("消费者发现节点【{}】的可用服务{}", servicePath, inetSocketAddresses.get(0));

        // 返回解析后的服务地址列表
        return inetSocketAddresses;
    }

}
