//package com.yy.channelHandler.handler;
//
//
//import com.yy.MrpcBootStrap;
//import com.yy.transport.MrpcResponse;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import lombok.extern.slf4j.Slf4j;
//
//import java.net.SocketAddress;
//import java.util.concurrent.CompletableFuture;
//
//@Slf4j
//public class ConsumerChannelInboundHandler extends SimpleChannelInboundHandler<MrpcResponse> {
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MrpcResponse mrpcResponse) throws Exception {
//        CompletableFuture<Object> completableFuture = MrpcBootStrap.PENDING_FUTURE.get(mrpcResponse.getRequestId());
//
//        SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();
//        Map<SocketAddress, CircuitBreaker> everyIpCircuitBreaker = MrpcBootStrap.getInstance()
//                .getConfiguration().getEveryIpCircuitBreaker();
//        CircuitBreaker circuitBreaker = everyIpCircuitBreaker.get(socketAddress);
//
//        byte code = mrpcResponse.getCode();
//        if(code == ResCode.FAIL.getCode()){
//            circuitBreaker.recordErrorRequest();
//            completableFuture.complete(null);
//            log.error("当前id为[{}]的请求，返回错误的结果，响应码[{}].",
//                    mrpcResponse.getRequestId(),mrpcResponse.getCode());
//            throw new ResponseException(code,ResCode.FAIL.getStatus());
//
//        } else if (code == ResCode.RATE_LIMIT.getCode()){
//            circuitBreaker.recordErrorRequest();
//            completableFuture.complete(null);
//            log.error("当前id为[{}]的请求，被限流，响应码[{}].",
//                    mrpcResponse.getRequestId(),mrpcResponse.getCode());
//            throw new ResponseException(code,ResCode.RATE_LIMIT.getStatus());
//
//        } else if (code == ResCode.RESOURCE_NOT_FOUND.getCode() ){
//            circuitBreaker.recordErrorRequest();
//            completableFuture.complete(null);
//            log.error("当前id为[{}]的请求，未找到目标资源，响应码[{}].",
//                    mrpcResponse.getRequestId(),mrpcResponse.getCode());
//            throw new ResponseException(code,ResCode.RESOURCE_NOT_FOUND.getStatus());
//
//        } else if (code == ResCode.SUCCESS.getCode() ){
//            // 服务提供方，给予的结果
//            Object returnValue = mrpcResponse.getBody();
//            completableFuture.complete(returnValue);
//            log.debug("以寻找到编号为【{}】的completableFuture，处理响应结果。", mrpcResponse.getRequestId());
//        } else if (code == ResCode.SUCCESS_HEART_BEAT.getCode()){
//            completableFuture.complete(null);
//            log.debug("以寻找到编号为【{}】的completableFuture，处理心跳检测，处理响应结果。", mrpcResponse.getRequestId());
//        }
//    }
//}
