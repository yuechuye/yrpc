package com.yy.proxy.handler;


import com.yy.NettyScope;
import com.yy.RpcBootStrap;
import com.yy.common.exceptions.NetException;
import com.yy.compress.CompressorFactory;
import com.yy.discovery.Registry;
import com.yy.enumerate.RequestType;
import com.yy.protect.CircuitBreaker;
import com.yy.serializer.SerializeFactory;
import com.yy.transport.RequestPayload;
import com.yy.transport.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 代理对象的调用处理器，负责执行远程调用。
 * 通过动态代理机制，当调用代理对象的方法时，会转由本类的invoke方法处理。
 */
@Slf4j
public class ConsumerInvocationHandler implements InvocationHandler {

    private Registry registry; // 服务发现组件
    private Class<?> interfaceRef; // 代理的接口类
    private String group; // 服务分组

    /**
     * 创建消费者调用处理器。
     *
     * @param registry        服务发现注册中心
     * @param interfaceRef    代理的接口类
     * @param group           服务分组
     */
    public ConsumerInvocationHandler(Registry registry, Class<?> interfaceRef, String group) {
        this.registry = registry;
        this.interfaceRef = interfaceRef;
        this.group = group;
    }

    /**
     * 当调用代理对象的方法时，会执行本方法。
     * 负责将方法调用转换为远程通信过程。
     *
     * @param proxy 代理对象
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 方法执行过程中抛出的任何异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.debug("method:" + method.getName());
        log.debug("args:" + Arrays.toString(args));

        // 最大重试次数
        int tryTimes = 3;
        // 重试间隔时间
        int intervalTime = 2000;

        while (true) {
            // 封装请求Payload
            RequestPayload requestPayload = RequestPayload.builder()
                    .interfaceName(interfaceRef.getName())
                    .methodName(method.getName())
                    .paramType(method.getParameterTypes())
                    .paramValue(args)
                    .returnType(method.getReturnType())
                    .build();

            // 创建MRPC请求对象
            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId(RpcBootStrap.getInstance().getConfiguration().getIdGenerator().getId())
                    .requestType(RequestType.REQUEST.getI())
                    .compressType(CompressorFactory
                            .getCompressorWrapper(RpcBootStrap.getInstance().getConfiguration().getCompressType())
                            .getCode())
                    .serializeType(SerializeFactory
                            .getSerialize(RpcBootStrap.getInstance().getConfiguration().getSerializeType())
                            .getCode())
                    .requestPayload(requestPayload)
                    .build();
            RpcBootStrap.THREAD_LOCAL_CACHE.set(rpcRequest);

            // 从注册中心获取服务地址
            InetSocketAddress address =
                    RpcBootStrap.getInstance()
                            .getConfiguration().getBalancer()
                            .selectServiceAddress(interfaceRef.getName(),group);
            log.debug("拿到了可用服务【{}】",address);

            // 获取或创建对应服务地址的熔断器
            Map<SocketAddress, CircuitBreaker> everyIpCircuitBreaker = RpcBootStrap.getInstance()
                    .getConfiguration().getEveryIpCircuitBreaker();
            CircuitBreaker circuitBreaker = everyIpCircuitBreaker.get(address);
            if (circuitBreaker == null) {
                circuitBreaker = new CircuitBreaker(1,0.5F);
                everyIpCircuitBreaker.put(address,circuitBreaker);
            }

            try {
                // 如果熔断器打开且请求非心跳，则尝试重试
                if (circuitBreaker.isBreak() && rpcRequest.getRequestType() != RequestType.HEARTBEAT.getI()){
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            RpcBootStrap.getInstance()
                                    .getConfiguration()
                                    .getEveryIpCircuitBreaker()
                                    .get(address).reset();
                        }
                    }, 5000);
                    throw new RuntimeException("熔断器已开启，无法发送请求");
                }

                // 获取通道并发送请求
                Channel channel = getChannel(address);
                log.debug("与【{}】建立了可用的通道",address);

                CompletableFuture<Object> completableFuture = new CompletableFuture<>();

                RpcBootStrap.PENDING_FUTURE.put(rpcRequest.getRequestId(), completableFuture);

                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) promise ->{
                    if (!promise.isSuccess()){
                        completableFuture.completeExceptionally(promise.cause());
                    }
                });

                // 等待响应并返回结果
                Object result = completableFuture.get(10, TimeUnit.SECONDS);
                circuitBreaker.recordRequest();

                return result;
            } catch (RuntimeException | InterruptedException | ExecutionException | TimeoutException e) {
                // 处理重试逻辑
                // 次数减一，并且等待固定时间
                tryTimes--;
                circuitBreaker.recordErrorRequest();
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException ex) {
                    log.error("在进行重试时发生异常.", ex);
                }
                // 如果重试次数用尽，抛出异常
                if (tryTimes < 0) {
                    log.error("对方法【{}】进行远程调用时，重试{}次，依然不可调用",
                            method.getName(), tryTimes, e);
                    break;
                }
                log.error("在进行第{}次重试时发生异常.", 3 - tryTimes, e);
            }
        }
        throw new RuntimeException("执行远程方法" + method.getName() + "调用失败。");
    }

    /**
     * 获取与指定服务地址对应的通道。
     * 如果通道不存在，则尝试建立新的连接。
     *
     * @param address 服务地址
     * @return 通道对象
     * @throws InterruptedException 获取通道过程中发生的中断异常
     * @throws ExecutionException 获取通道过程中发生的执行异常
     * @throws TimeoutException 获取通道超时
     */
    private static Channel getChannel(InetSocketAddress address) throws InterruptedException, ExecutionException, TimeoutException {
        Channel channel = RpcBootStrap.CHANNEL_CACHE.get(address);
        if (channel == null) {
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            NettyScope.getInstance().connect(address)
                    .addListener((ChannelFutureListener) promise ->{
                        if (promise.isDone()) {
                            log.debug("已经和【{}】建立连接", address);
                            completableFuture.complete(promise.channel());
                        } else if (!promise.isSuccess()){
                            completableFuture.completeExceptionally(promise.cause());
                        }
                    });

            channel = completableFuture.get(1, TimeUnit.SECONDS);

            RpcBootStrap.CHANNEL_CACHE.put(address, channel);
        }
        if (channel == null) {
            log.debug("获取或与【{}】建立通道时发生了异常",address);
            throw new NetException("获取channel失败");
        }
        return channel;
    }
}
