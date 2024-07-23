package com.yy.watcher;


import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @author yuechu
 */
@Slf4j
public class UpAndDownWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
            log.debug("检测到服务【{}】下有节点上/下线，重新拉取列表。",watchedEvent.getPath());
            //todo重新拉取服务信息
        }
    }

    private String getServiceName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
