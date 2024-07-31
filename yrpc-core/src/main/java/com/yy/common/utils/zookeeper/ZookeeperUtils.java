package com.yy.common.utils.zookeeper;


import com.yy.common.Constant;
import com.yy.common.exceptions.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper工具类
 */
@Slf4j
public class ZookeeperUtils {


    /**
     * 默认zookeeper创建方式
     * @return zookeeper实例
     */
    public static ZooKeeper zooKeeperCreate(){
        String connectString = Constant.DEFAULT_ZK_CONNECT;
        int timeout = Constant.DEFAULT_ZK_TIMEOUT;
        return zookeeperCreate(connectString,timeout);
    }


    /**
     * 入参zookeeper创建方式
     * @return zookeeper实例
     */
    public static ZooKeeper zookeeperCreate(String connectString, int timeout){
        CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            final ZooKeeper zooKeeper = new ZooKeeper(connectString,timeout,event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    log.debug("客户端连接成功。。。");
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();

            return zooKeeper;

        } catch (IOException | InterruptedException e) {
            log.error("创建时出现异常：",e);
            throw new ZookeeperException();
        }
    }

    /**
     * 创建一个节点的工具方法
     * @param zooKeeper 实例
     * @param node 节点
     * @param watcher 观察者
     * @param createMode 创建模式（节点类型）
     * @return 成功true 其他false
     */
    public static boolean CreateNode(ZooKeeper zooKeeper, ZookeeperNode node, Watcher watcher,
                                     CreateMode createMode) {
        try {
            // Split the path into components
            String[] pathComponents = node.getNodePath().split("/");
            String currentPath = "";

            for (String component : pathComponents) {
                if (!component.isEmpty()) { // Skip empty components due to leading/trailing slashes
                    currentPath += "/" + component;

                    // Check if the current path exists
                    if (zooKeeper.exists(currentPath, watcher) == null) {
                        // If not, create it with an empty data and open ACL
                        zooKeeper.create(currentPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        log.debug("节点【{}】创建成功", currentPath);
                    }
                }
            }

            // Update the currentPath to be the full node path
            currentPath = node.getNodePath();

            // Now that all parent nodes exist, we can safely create the final node with its data
            if (zooKeeper.exists(currentPath, watcher) == null) {
                zooKeeper.create(currentPath, node.getData(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.debug("节点【{}】创建成功", currentPath);
                return true;
            } else {
                log.debug("节点【{}】已经存在", currentPath);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建节点时发生异常：", e);
            throw new ZookeeperException();
        }
        return false;
    }

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
            throw new ZookeeperException("获取节点子元素时发生异常");
        }
    }
}
