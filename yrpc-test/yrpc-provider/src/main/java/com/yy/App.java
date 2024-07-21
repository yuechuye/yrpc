package com.yy;

import com.yy.discover.impl.ZookeeperRegistry;
import com.yy.utils.zookeeper.ZookeeperNode;
import com.yy.utils.zookeeper.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * @author yuechu
 */
public class App {
    public static void main(String[] args) {


        nodeCreaste();
        nodeList();


    }

    private static void nodeList() {
        ZooKeeper zooKeeper = ZookeeperUtils.zooKeeperCreate();
        String servicePath = "/rpc-yy";
        List<String> children = ZookeeperUtils.getChildren(zooKeeper, servicePath, null);
        System.out.println(children);
    }

    private static void nodeCreaste() {
        ZooKeeper zooKeeper = ZookeeperUtils.zooKeeperCreate();

        //创建根
        String servicePath ="/rpc-yy";
        ZookeeperNode serviceNode = new ZookeeperNode(servicePath, null);
        ZookeeperUtils.CreateNode(zooKeeper,serviceNode,null, CreateMode.PERSISTENT);

        //创建三个子
        String node1 ="/node1";
        String node2 ="/node2";
        String node3 ="/node3";
        ZookeeperNode serviceNode1 = new ZookeeperNode(servicePath + node1, null);
        ZookeeperNode serviceNode2 = new ZookeeperNode(servicePath + node2, null);
        ZookeeperNode serviceNode3 = new ZookeeperNode(servicePath + node3, null);
        ZookeeperUtils.CreateNode(zooKeeper,serviceNode1,null, CreateMode.PERSISTENT);
        ZookeeperUtils.CreateNode(zooKeeper,serviceNode2,null, CreateMode.PERSISTENT);
        ZookeeperUtils.CreateNode(zooKeeper,serviceNode3,null, CreateMode.PERSISTENT);

    }

    private static void serviceRegister() {
        ServiceConfig<HelloMrpcService> service = new ServiceConfig();
        service.setInterface(HelloMrpcService.class);
        service.setRef(new HelloMrpcServiceImpl());

        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
        zookeeperRegistry.registry(service);
    }


}
