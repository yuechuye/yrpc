package com.yy.balance;

import com.yy.RpcBootStrap;
import com.yy.transport.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 基于一致性哈希算法的负载均衡器。
 * 一致性哈希算法能够使得在节点添加或删除时，尽量少地改变已存在的请求到服务器的映射。
 */
@Slf4j
public class ConsistentHashBalancer extends AbstractBalancer {

    /**
     * 获取选择器。
     * 该选择器基于一致性哈希算法，用于将请求分配给后端服务器。
     *
     * @param serviceList 服务器列表
     * @return 一致性哈希选择器
     */
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new ConsistentHashSelector(serviceList, 128);
    }

    /**
     * 一致性哈希选择器。
     * 该类负责根据一致性哈希算法从服务器列表中选择合适的服务器。
     */
    public class ConsistentHashSelector implements Selector {

        /**
         * 哈希环，使用TreeMap实现，保证按照哈希值的顺序存储节点。
         */
        private SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();

        /**
         * 每个物理节点对应的虚拟节点数量。
         * 增加虚拟节点数可以提高哈希环的分布均匀度。
         */
        private int virtualNodes;

        /**
         * 构造函数。
         *
         * @param serviceList 服务器列表
         * @param virtualNodes 每个物理节点对应的虚拟节点数量
         */
        public ConsistentHashSelector(List<InetSocketAddress> serviceList, int virtualNodes) {
            this.virtualNodes = virtualNodes;
            for (InetSocketAddress inetSocketAddress : serviceList) {
                addCircle(inetSocketAddress);
            }
        }

        /**
         * 将物理节点添加到哈希环中。
         * 通过为每个物理节点添加多个虚拟节点，来提高哈希环的分布均匀度。
         *
         * @param inetSocketAddress 物理节点
         */
        private void addCircle(InetSocketAddress inetSocketAddress) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(inetSocketAddress.toString() + "-" + i);
                circle.put(hash, inetSocketAddress);
                log.debug("hash值为【{}】的节点已经挂载到了哈希环上", hash);
            }
        }

        /**
         * 根据请求的唯一标识符选择服务器。
         * 使用一致性哈希算法确定请求应该路由到哪个服务器。
         *
         * @return 选中的服务器
         */
        @Override
        public InetSocketAddress getNext() {
            RpcRequest RpcRequest = RpcBootStrap.THREAD_LOCAL_CACHE.get();
            String requestId = Long.toString(RpcRequest.getRequestId());
            int hash = hash(requestId);
            if (!circle.containsKey(hash)) {
                SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        }

        /**
         * 计算字符串的哈希值。
         * 使用MD5算法计算字符串的哈希值，用于一致性哈希算法。
         *
         * @param s 输入的字符串
         * @return 哈希值
         */
        public int hash(String s) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(s.getBytes());
                int res = 0;
                for (int i = 0; i < 4; i++) {
                    res = res << 8;
                    if (digest[i] < 0) {
                        res = res | (digest[i] & 255);
                    } else {
                        res = res | digest[i];
                    }
                }
                return res;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
