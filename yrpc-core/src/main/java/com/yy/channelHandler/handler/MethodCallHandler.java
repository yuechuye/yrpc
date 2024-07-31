package com.yy.channelHandler.handler;


import com.yy.RpcBootStrap;
import com.yy.ServiceConfig;
import com.yy.enumerate.RequestType;
import com.yy.enumerate.ResCode;
import com.yy.protect.RateLimiter;
import com.yy.protect.TokenBuketRateLimiter;
import com.yy.transport.RequestPayload;
import com.yy.transport.RpcRequest;
import com.yy.transport.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

@Slf4j
/**
 * 处理RPC请求的处理器，继承自SimpleChannelInboundHandler，专门处理RpcRequest类型的消息。
 */
public class MethodCallHandler extends SimpleChannelInboundHandler<RpcRequest> {
    /**
     * 当有消息读取时的处理方法。
     *
     * @param channelHandlerContext Netty的上下文对象，用于通道的读写操作。
     * @param rpcRequest            接收到的RPC请求对象。
     * @throws Exception 如果处理过程中发生异常。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // 创建RPC响应对象，设置与请求相关的属性。
        // 封装响应
        RpcResponse rpcResponse = RpcResponse.builder()
                .compressType(rpcRequest.getCompressType())
                .serializeType(rpcRequest.getSerializeType())
                .requestId(rpcRequest.getRequestId())
                .timeStrap(rpcRequest.getTimeStrap())
                .build();

        // 获取发送请求的通道。
        // 获取通道
        Channel channel = channelHandlerContext.channel();

        // 实现限流机制。
        // 完成限流操作
        SocketAddress socketAddress = channel.remoteAddress();
        Map<SocketAddress, RateLimiter> ipRateLimiter = RpcBootStrap.getInstance().getConfiguration().getEveryIpRateLimiter();
        RateLimiter rateLimiter = ipRateLimiter.get(socketAddress);
        if (rateLimiter == null) {
            rateLimiter = new TokenBuketRateLimiter(10, 10);
            ipRateLimiter.put(socketAddress, rateLimiter);
        }
        boolean allowRequest = rateLimiter.allowRequest();

        // 根据限流结果或请求类型设置响应码。
        // 处理请求
        if (!allowRequest) {
            // 限流
            rpcResponse.setCode(ResCode.RATE_LIMIT.getCode());
        } else if (rpcRequest.getRequestType() == RequestType.HEARTBEAT.getI()) {
            // 心跳
            rpcResponse.setCode(ResCode.SUCCESS_HEART_BEAT.getCode());
        } else {
            // 普通请求
            try {
                // 调用目标方法，并设置响应体。
                // 获取负载
                RequestPayload requestPayload = rpcRequest.getRequestPayload();
                Object object = null;
                // 进行方法调用
                if (requestPayload != null) {
                    object = callTargetMethod(requestPayload);
                    log.debug("请求【{}】已经在服务端完成方法调用", rpcRequest.getRequestId());
                }
                rpcResponse.setBody(object);
                rpcResponse.setCode(ResCode.SUCCESS.getCode());
            } catch (Exception e) {
                log.error("编号为【{}】的请求在调用过程中发生异常。", rpcRequest.getRequestId(), e);
                rpcResponse.setCode(ResCode.FAIL.getCode());
            }
        }

        // 将响应写入通道。
        // 写出响应
        channel.writeAndFlush(rpcResponse);
    }

    /**
     * 调用目标方法。
     * 根据请求负载中的接口名、方法名、参数类型和值，找到对应的服务实例和方法，并执行。
     *
     * @param requestPayload RPC请求的负载信息，包含方法调用的相关参数。
     * @return 方法的返回值。
     */
    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] paramType = requestPayload.getParamType();
        Object[] paramValue = requestPayload.getParamValue();
        ServiceConfig<?> service = RpcBootStrap.SERVICE_LIST.get(interfaceName);
        Object ref = service.getRef();
        Class<?> aClass = ref.getClass();
        try {
            Method method = aClass.getMethod(methodName, paramType);
            return method.invoke(ref, paramValue);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用服务【{}】的【{}】方法时发生异常", interfaceName, methodName, e);
            throw new RuntimeException(e);
        }
    }
}
