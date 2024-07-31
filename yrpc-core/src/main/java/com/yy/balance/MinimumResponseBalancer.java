/**
 * 最小响应时间负载均衡器，继承自AbstractBalancer。
 * 该类实现了基于最小响应时间的服务器选择算法，用于RpcBootStrap的服务器列表中选择一个响应时间最短的服务器。
 */
package com.yy.balance;


import com.yy.RpcBootStrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class MinimumResponseBalancer extends AbstractBalancer{

    /**
     * 获取一个选择器，用于从服务列表中选择下一个服务器。
     *
     * @param serviceList 服务地址列表，提供Rpc服务的服务器列表。
     * @return 返回一个Selector实例，用于实际的选择逻辑。
     */
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new MinimumResponseSelector(serviceList);
    }

    /**
     * 最小响应时间选择器，实现了Selector接口。
     * 该选择器通过比较服务器的响应时间，选择当前响应时间最短的服务器。
     */
    private class MinimumResponseSelector implements Selector{

        private List<InetSocketAddress> serviceList;

        /**
         * 构造函数，初始化服务地址列表。
         *
         * @param serviceList 服务地址列表，提供Rpc服务的服务器列表。
         */
        public MinimumResponseSelector(List<InetSocketAddress> serviceList){
            this.serviceList = serviceList;
        }

        /**
         * 获取下一个服务器地址。
         * 该方法选择响应时间最短的服务器，如果RpcBootStrap的RES_TIME_CHANNEL_CACHE中没有数据，则从CHANNEL_CACHE中随机选择一个服务器。
         *
         * @return 返回下一个应选择的服务器地址。
         */
        @Override
        public InetSocketAddress getNext() {
            Map.Entry<Long, Channel> entry = RpcBootStrap.RES_TIME_CHANNEL_CACHE.firstEntry();
            if (entry != null){
                return (InetSocketAddress) entry.getValue().remoteAddress();
            }
            return (InetSocketAddress) RpcBootStrap.CHANNEL_CACHE.keySet().toArray()[0];
        }
    }
}
