package com.yy.utils.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ZookeeperNode 类代表 Zookeeper 中的一个节点。
 * @author yuechu
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {
    /**
     * 节点的路径。
     * <p>
     * 节点路径在 Zookeeper 中是唯一的标识符，用于定位和访问特定的节点。
     */
    private String nodePath;

    /**
     * 节点的数据。
     * <p>
     * 节点数据是存储在 Zookeeper 节点中的二进制数据，可以被读取和更新。
     */
    private byte[] data;
}
