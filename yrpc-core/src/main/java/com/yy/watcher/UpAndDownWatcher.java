package com.yy.watcher;


import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

@Slf4j
public class UpAndDownWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
            log.debug("检测到服务【{}】下有节点上/下线，重新拉取列表。",watchedEvent.getPath());
//            String serviceName = getServiceName(watchedEvent.getPath());
//            获取列表并打印


//            List<InetSocketAddress> serviceList =
//                    MrpcBootStrap.getInstance()
//                            .getConfiguration()
//                            .getRegistryConfig().getRegistry().lookUp(serviceName, MrpcBootStrap.getInstance()
//                                    .getConfiguration().getGroup());
//            //处理动态上线
//            for (InetSocketAddress address : serviceList) {
//                //处理新增节点
//                if (!MrpcBootStrap.CHANNEL_CACHE.containsKey(address)){
//                    //根据地址建立连接，并缓存
//                    try {
//                        Channel channel = NettyScope.getInstance().connect(address).sync().channel();
//                        MrpcBootStrap.CHANNEL_CACHE.put(address,channel);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//            //处理动态下线
//            for (Map.Entry<InetSocketAddress, Channel> entry : MrpcBootStrap.CHANNEL_CACHE.entrySet()) {
//                if (!serviceList.contains(entry.getKey())){
//                    MrpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
//                }
//            }
//            //发生动态上下线时更新负载均衡器列表
//            MrpcBootStrap.getInstance()
//                    .getConfiguration().getBalancer().reLoadBalance(serviceName,serviceList);
        }
    }

    private String getServiceName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
