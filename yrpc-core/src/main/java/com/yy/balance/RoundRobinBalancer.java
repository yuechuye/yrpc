package com.yy.balance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器的实现类，用于在多个服务提供者之间进行负载均衡。
 */
public class RoundRobinBalancer extends AbstractBalancer{

    /**
     * 根据服务列表创建并返回一个轮询选择器。
     *
     * @param serviceList 服务提供者的地址列表。
     * @return 返回一个轮询选择器，用于选择下一个服务提供者。
     */
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new RoundRobinSelector(serviceList);
    }

    /**
     * 轮询选择器的实现类，用于按照轮询算法选择下一个服务提供者。
     */
    public class RoundRobinSelector implements Selector{

        /**
         * 用于跟踪当前选择的服务提供者的索引。
         */
        private AtomicInteger index;

        /**
         * 服务提供者的地址列表。
         */
        private List<InetSocketAddress> serviceList;

        /**
         * 初始化轮询选择器，设置服务提供者列表和初始索引。
         *
         * @param serviceList 服务提供者的地址列表。
         */
        public RoundRobinSelector(List<InetSocketAddress> serviceList) {
            this.serviceList = serviceList;
            this.index = new AtomicInteger(0);
        }

        /**
         * 根据轮询算法选择并返回下一个服务提供者的地址。
         *
         * @return 返回下一个服务提供者的地址。
         */
        @Override
        public InetSocketAddress getNext() {
            InetSocketAddress address = serviceList.get(index.get());
            if (index.get() == serviceList.size() - 1){
                index.set(0);
            } else{
                index.incrementAndGet();
            }
            return address;
        }
    }
}
