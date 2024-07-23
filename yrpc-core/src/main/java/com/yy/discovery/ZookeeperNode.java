package com.yy.discovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuechu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {
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