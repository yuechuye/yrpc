package com.yy.utils.zookeeper;


import com.yy.Constant;
import com.yy.exception.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper工具类，提供Zookeeper客户端创建、节点创建、节点子元素获取等操作。
 */
@Slf4j
public class ZookeeperUtils {


    /**
     * 使用默认配置创建Zookeeper客户端。
     * <p>
     * 默认配置包括连接字符串和会话超时时间。
     *
     * @return Zookeeper客户端实例。
     */
    public static ZooKeeper zooKeeperCreate(){
        String connectString = Constant.DEFAULT_ZK_CONNECT;
        int timeout = Constant.DEFAULT_ZK_TIMEOUT;
        return zookeeperCreate(connectString,timeout);
    }


    /**
     * 创建Zookeeper客户端。
     * <p>
     * 通过提供的连接字符串和会话超时时间创建Zookeeper客户端，并确保客户端成功连接到Zookeeper。
     *
     * @param connectString Zookeeper的连接字符串。
     * @param timeout        会话超时时间。
     * @return Zookeeper客户端实例。
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
     * 在Zookeeper中创建一个节点。
     * <p>
     * 如果节点不存在，则创建节点并返回true；如果节点已存在，则不创建节点并返回false。
     *
     * @param zooKeeper     Zookeeper客户端实例。
     * @param node          要创建的节点信息，包括节点路径和节点数据。
     * @param watcher       节点的观察者，用于监听节点变化。
     * @param createMode    节点的创建模式，定义了节点的类型和持久性。
     * @return 如果节点成功创建，返回true；否则返回false。
     */
    public static boolean CreateNode(ZooKeeper zooKeeper, ZookeeperNode node, Watcher watcher,
                                     CreateMode createMode){
        try {
            if (zooKeeper.exists(node.getNodePath(), watcher) == null) {
                zooKeeper.create(node.getNodePath(), node.getData(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.debug("节点【{}】创建成功", node.getNodePath());
                return true;
            } else {
                log.debug("节点【{}】已经存在", node.getNodePath());
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建节点时发生异常：", e);
            throw new ZookeeperException();
        }
        return false;
    }


    /**
     * 关闭指定的Zookeeper客户端实例。
     * <p>
     * 此方法确保在关闭客户端时处理任何中断异常。
     *
     * @param zooKeeper Zookeeper客户端实例。
     */
    public static void close(ZooKeeper zooKeeper){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            log.error("关闭时发生异常：", e);
            throw new ZookeeperException();
        }
    }

    /**
     * 获取指定节点的子节点列表。
     * <p>
     * 可以指定一个观察者来监听子节点的变化。
     *
     * @param zooKeeper     Zookeeper客户端实例。
     * @param servicePath   要获取子节点的节点路径。
     * @param watcher       子节点变化的观察者。
     * @return 子节点的名称列表。
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
