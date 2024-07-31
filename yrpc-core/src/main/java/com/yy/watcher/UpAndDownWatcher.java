package com.yy.watcher;


import com.yy.NettyScope;
import com.yy.RpcBootStrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

@Slf4j
/**
 * 实现Watcher接口，用于监控服务的上下线变化。
 */
public class UpAndDownWatcher implements Watcher {
    /**
     * 处理监控事件。
     * 当检测到服务下有节点上/下线时，重新拉取节点列表，并根据列表的变更动态更新连接和负载均衡器。
     *
     * @param watchedEvent 监控事件。
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        // 只处理节点子项变更事件
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            // 日志记录服务上下线变化
            log.debug("检测到服务【{}】下有节点上/下线，重新拉取列表。", watchedEvent.getPath());
            // 从路径中提取服务名
            String serviceName = getServiceName(watchedEvent.getPath());
            // 获取服务列表
            List<InetSocketAddress> serviceList =
                    RpcBootStrap.getInstance()
                            .getConfiguration()
                            .getRegistryConfig().getRegistry().lookUp(serviceName, RpcBootStrap.getInstance()
                                    .getConfiguration().getGroup());
            // 处理新增的服务节点
            // 处理动态上线
            for (InetSocketAddress address : serviceList) {
                // 如果缓存中不存在，则建立连接并缓存
                // 处理新增节点
                if (!RpcBootStrap.CHANNEL_CACHE.containsKey(address)){
                    // 根据地址建立连接，并缓存
                    try {
                        Channel channel = NettyScope.getInstance().connect(address).sync().channel();
                        RpcBootStrap.CHANNEL_CACHE.put(address,channel);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            // 处理下线的服务节点
            // 处理动态下线
            for (Map.Entry<InetSocketAddress, Channel> entry : RpcBootStrap.CHANNEL_CACHE.entrySet()) {
                // 如果当前服务列表中不存在，则从缓存中移除
                if (!serviceList.contains(entry.getKey())){
                    RpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                }
            }
            // 更新负载均衡器的服务列表
            // 发生动态上下线时更新负载均衡器列表
            RpcBootStrap.getInstance()
                    .getConfiguration().getBalancer().reLoadBalance(serviceName,serviceList);
        }
    }

    /**
     * 从路径中提取服务名称。
     *
     * @param path 服务的注册中心路径。
     * @return 服务名称。
     */
    private String getServiceName(String path) {
        // 使用路径分隔符分割路径，并返回最后一段作为服务名称
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
