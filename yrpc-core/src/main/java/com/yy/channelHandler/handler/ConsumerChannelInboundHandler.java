package com.yy.channelHandler.handler;


import com.yy.RpcBootStrap;
import com.yy.common.exceptions.ResponseException;
import com.yy.enumerate.ResCode;
import com.yy.protect.CircuitBreaker;
import com.yy.transport.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
/**
 * 消费者通道入站处理器，用于处理RPC响应。
 * 继承自SimpleChannelInboundHandler，专门处理RpcResponse类型的消息。
 */
public class ConsumerChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    /**
     * 当通道读取到数据时的处理方法。
     * 根据RpcResponse的代码字段决定如何处理响应。
     *
     * @param channelHandlerContext ChannelHandlerContext，用于通道操作和事件传播。
     * @param rpcResponse           RpcResponse，包含RPC调用的响应信息。
     * @throws Exception 如果处理过程中发生异常。
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        // 根据请求ID获取对应的CompletableFuture，用于最终完成异步处理。
        CompletableFuture<Object> completableFuture = RpcBootStrap.PENDING_FUTURE.get(rpcResponse.getRequestId());

        // 获取响应来源的远程地址，用于后续的熔断器策略。
        SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();

        // 获取针对每个IP的熔断器实例。
        Map<SocketAddress, CircuitBreaker> everyIpCircuitBreaker = RpcBootStrap.getInstance()
                .getConfiguration().getEveryIpCircuitBreaker();
        CircuitBreaker circuitBreaker = everyIpCircuitBreaker.get(socketAddress);

        // 根据响应代码进行不同情况的处理。
        byte code = rpcResponse.getCode();
        if (code == ResCode.FAIL.getCode()) {
            // 如果响应代码表示失败，则记录错误请求，完成Future并抛出异常。
            circuitBreaker.recordErrorRequest();
            completableFuture.complete(null);
            log.error("当前id为[{}]的请求，返回错误的结果，响应码[{}].",
                    rpcResponse.getRequestId(), rpcResponse.getCode());
            throw new ResponseException(code, ResCode.FAIL.getStatus());
        } else if (code == ResCode.RATE_LIMIT.getCode()) {
            // 如果响应代码表示限流，则记录错误请求，完成Future并抛出异常。
            circuitBreaker.recordErrorRequest();
            completableFuture.complete(null);
            log.error("当前id为[{}]的请求，被限流，响应码[{}].",
                    rpcResponse.getRequestId(), rpcResponse.getCode());
            throw new ResponseException(code, ResCode.RATE_LIMIT.getStatus());
        } else if (code == ResCode.RESOURCE_NOT_FOUND.getCode()) {
            // 如果响应代码表示资源未找到，则记录错误请求，完成Future并抛出异常。
            circuitBreaker.recordErrorRequest();
            completableFuture.complete(null);
            log.error("当前id为[{}]的请求，未找到目标资源，响应码[{}].",
                    rpcResponse.getRequestId(), rpcResponse.getCode());
            throw new ResponseException(code, ResCode.RESOURCE_NOT_FOUND.getStatus());
        } else if (code == ResCode.SUCCESS.getCode()) {
            // 如果响应代码表示成功，则提取响应体，完成Future。
            // 服务提供方，给予的结果
            Object returnValue = rpcResponse.getBody();
            completableFuture.complete(returnValue);
            log.debug("以寻找到编号为【{}】的completableFuture，处理响应结果。", rpcResponse.getRequestId());
        } else if (code == ResCode.SUCCESS_HEART_BEAT.getCode()) {
            // 如果响应代码表示心跳，则直接完成Future。
            completableFuture.complete(null);
            log.debug("以寻找到编号为【{}】的completableFuture，处理心跳检测，处理响应结果。", rpcResponse.getRequestId());
        }
    }
}
