package com.yy.heartbeat;


import com.yy.NettyScope;
import com.yy.RpcBootStrap;
import com.yy.compress.CompressorFactory;
import com.yy.enumerate.RequestType;
import com.yy.serializer.SerializeFactory;
import com.yy.transport.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 心跳检测器
 */
@Slf4j
public class HeartBeatDetector {

    /**
     * 检测指定服务的心跳状态。
     * 通过向服务提供者发送心跳请求，以验证网络连接是否畅通，确保服务的可用性。
     *
     * @param serviceName 要检测心跳的服务名称。
     */
    public static void detectHeartbeat(String serviceName){
        // 从注册中心获取服务提供者的地址列表
        // 从注册中心获取服务列表
        List<InetSocketAddress> serviceList =
                RpcBootStrap.getInstance()
                        .getConfiguration()
                        .getRegistryConfig().getRegistry().lookUp(serviceName, RpcBootStrap.getInstance()
                                .getConfiguration().getGroup());
        // 清除上一次心跳检测的响应时间缓存
        // 每次心跳开始前清除响应时间缓存的内容
        RpcBootStrap.RES_TIME_CHANNEL_CACHE.clear();
        // 遍历服务列表，尝试连接每个服务提供者
        // 将连接进行缓存
        for (InetSocketAddress address : serviceList) {
            try {
                // 如果尚未建立连接，则创建一个新的连接
                if (!RpcBootStrap.CHANNEL_CACHE.containsKey(address)) {
                    Channel channel = NettyScope.getInstance().connect(address).sync().channel();
                    RpcBootStrap.CHANNEL_CACHE.put(address,channel);
                }
            } catch (InterruptedException e) {
                // 异常情况下终止程序
                throw new RuntimeException(e);
            }
        }
        // 定时发送心跳请求，以检测与服务提供者的连接状态
        // 使用Timer定时任务，以固定频率发送心跳请求来检测和服务端的连接状态
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 遍历所有已建立的连接，发送心跳请求
                // 获取通道缓存，用于遍历所有已建立的连接
                Map<InetSocketAddress, Channel> channelCache = RpcBootStrap.CHANNEL_CACHE;
                for (Map.Entry<InetSocketAddress, Channel> entry : channelCache.entrySet()) {
                    // 尝试连接的最大次数
                    int tryTimes = 3;
                    while (true) {
                        // 构建心跳请求消息
                        // 获取通道，用于发送心跳请求
                        Channel channel = entry.getValue();

                        // 记录发送心跳请求的时间
                        long startTime = System.currentTimeMillis();

                        // 构建心跳请求
                        // 构建一个心跳请求
                        RpcRequest rpcRequest = RpcRequest.builder()
                                .requestId(RpcBootStrap.getInstance()
                                        .getConfiguration().getIdGenerator().getId())
                                .requestType(RequestType.REQUEST.getI())
                                .compressType(CompressorFactory.getCompressorWrapper(RpcBootStrap.getInstance()
                                        .getConfiguration().getCompressType()).getCode())
                                .serializeType(SerializeFactory.getSerialize(RpcBootStrap.getInstance()
                                        .getConfiguration().getSerializeType()).getCode())
                                .timeStrap(startTime)
                                .build();

                        // 发送心跳请求，并异步等待响应
                        // 创建一个CompletableFuture，用于异步处理心跳响应
                        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                        RpcBootStrap.PENDING_FUTURE.put(rpcRequest.getRequestId(), completableFuture);
                        // 发送心跳请求，并监听发送结果
                        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) promise -> {
                            if (!promise.isSuccess()) {
                                // 如果发送失败，标记异常并完成Future
                                completableFuture.completeExceptionally(promise.cause());
                            }
                        });

                        try {
                            // 等待心跳响应，超时时间为1秒
                            completableFuture.get(1, TimeUnit.SECONDS);
                            // 计算响应时间
                            long endTime = System.currentTimeMillis();
                            long time = endTime - startTime;
                            // 记录响应时间最快的通道
                            // 记录响应时间
                            log.debug("和服务器【{}】的响应时间耗时：{}", entry.getKey(), time);
                            // 将通道和响应时间记录到缓存中，用于后续的响应时间统计
                            RpcBootStrap.RES_TIME_CHANNEL_CACHE.put(time, channel);
                        } catch (InterruptedException | ExecutionException e) {
                            // 心跳响应被中断或出现异常，尝试重新发送
                            // 心跳响应被中断或出现异常，进行重试逻辑
                            if (tryTimes > 0) {
                                log.debug("与服务【{}】尝试连接失败,正在进行第【{}】次尝试..."
                                        , channel.remoteAddress(), (4 - tryTimes));
                                tryTimes--;
                                continue;
                            }
                            // 重试次数用尽，关闭连接
                            // 重试次数用尽，从缓存中移除该通道
                            RpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                        } catch (TimeoutException e) {
                            // 心跳响应超时，尝试重新发送
                            // 心跳响应超时，进行重试逻辑
                            if (tryTimes > 0) {
                                log.debug("与服务【{}】尝试连接失败,正在进行第【{}】次尝试..."
                                        , channel.remoteAddress(), (4 - tryTimes));
                                tryTimes--;
                                continue;
                            }
                            // 重试次数用尽，关闭连接
                            // 重试次数用尽，从缓存中移除该通道
                            RpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                        }
                        // 如果连接正常，跳出循环，处理下一个通道
                        break;
                    }
                }
            }
        }, 0, 2000);
    }
}
