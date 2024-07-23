package com.yy.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yuechu
 */
@Slf4j
public class ZookeeperUtils {

    /**
     * 创建一个与ZooKeeper服务器连接的客户端。
     *
     * @param connectString 连接字符串，指定ZooKeeper服务器的地址和端口。
     * @param timeout 连接超时时间，单位为毫秒。
     * @return 成功连接到ZooKeeper的客户端实例。
     * @throws RuntimeException 如果连接过程中发生IO异常或线程被中断，则抛出此异常。
     */
    public static ZooKeeper zookeeperCreate(String connectString, int timeout){
        // 使用CountDownLatch来同步连接完成的状态，初始计数为1。
        CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            // 创建ZooKeeper客户端，传入连接字符串、超时时间及连接状态监听器。
            final ZooKeeper zooKeeper = new ZooKeeper(connectString,timeout,event -> {
                // 当客户端成功连接到服务器时，触发回调。
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    log.debug("客户端连接成功。。。");
                    // 减少CountDownLatch的计数，表示连接已完成。
                    countDownLatch.countDown();
                }
            });

            // 等待CountDownLatch计数归零，即等待连接完成。
            countDownLatch.await();

            return zooKeeper;

        } catch (IOException | InterruptedException e) {
            // 记录连接过程中的异常，并抛出运行时异常。
            log.error("创建时出现异常：",e);
            throw new RuntimeException("创建时出现异常：",e);
        }
    }



    /**
     * 在ZooKeeper中创建节点。
     *
     * @param zooKeeper ZooKeeper客户端实例。
     * @param node 要创建的ZookeeperNode对象，包含节点路径和数据。
     * @param watcher Watcher对象，用于接收节点状态变化的通知。
     * @param createMode 节点的创建模式（持久、临时等）。
     * @return 如果节点成功创建，返回true；如果节点已存在或创建失败，返回false。
     */
    public static boolean CreateNode(ZooKeeper zooKeeper, ZookeeperNode node, Watcher watcher,
                                     CreateMode createMode) {
        try {
            // 将节点路径分割成多个部分
            String[] pathComponents = node.getNodePath().split("/");
            String currentPath = "";

            // 遍历路径的每一部分，确保所有父节点都存在
            for (String component : pathComponents) {
                if (!component.isEmpty()) {
                    currentPath += "/" + component;

                    // 如果当前路径对应的节点不存在，则创建该节点
                    if (zooKeeper.exists(currentPath, watcher) == null) {
                        zooKeeper.create(currentPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        log.debug("节点【{}】创建成功", currentPath);
                    }
                }
            }

            // 设置currentPath为完整的节点路径
            currentPath = node.getNodePath();

            // 最终检查目标节点是否已存在，如果不存在则创建
            if (zooKeeper.exists(currentPath, watcher) == null) {
                zooKeeper.create(currentPath, node.getData(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.debug("节点【{}】创建成功", currentPath);
                return true;
            } else {
                log.debug("节点【{}】已经存在", currentPath);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建节点时发生异常：", e);
            throw new RuntimeException("创建节点时发生异常：", e);
        }
        return false;
    }

    /**
     * 表示Zookeeper中的一个节点。
     * <p>
     * 该类封装了节点路径和节点数据，提供了一种简单的方式来表示和操作Zookeeper中的数据节点。
     * 节点路径是节点在Zookeeper中的唯一标识，数据则是节点所存储的信息。
     */
        /**
         * zookeeper节点
         */
        @Data
        public class ZookeeperNode{
            /**
             * 节点的路径。
             * <p>
             * 节点路径在Zookeeper中是唯一的，用于定位和访问特定的节点。
             */
            private String nodePath;

            /**
             * 节点的数据。
             * <p>
             * 节点数据是存储在节点上的信息，可以是任意字节序列。
             */
            private byte[] data;
        }

    /**
     * 获取ZooKeeper节点的子节点列表。
     *
     * 本函数通过ZooKeeper客户端，尝试获取指定路径节点的子节点列表。如果操作成功，将返回子节点的名称列表；
     * 如果发生异常，例如连接中断或权限不足，则会抛出ZookeeperException。
     *
     * @param zooKeeper ZooKeeper客户端实例，用于与ZooKeeper服务器通信。
     * @param servicePath 要查询子节点的ZooKeeper节点路径。
     * @param watcher 设置为null，表示不关注此事件；如果非null，则会在获取子节点列表时注册一个Watcher，用于监听该节点的子节点变化。
     * @return 返回指定节点的子节点名称列表。如果节点不存在或操作被中断，可能返回空列表。
     */
    /**
     * 获取一个节点的子元素
     * @param zooKeeper zk实例
     * @param servicePath 服务路径
     * @param watcher 观察者
     * @return 字符串集合
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String servicePath, Watcher watcher) {
        try {
            System.out.println(servicePath);
            return zooKeeper.getChildren(servicePath, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取节点【{}】子元素时发生异常", servicePath,e);
            throw new RuntimeException("获取节点子元素时发生异常");
        }
    }

}
