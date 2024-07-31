package com.yy.channelHandler;


import com.yy.channelHandler.handler.ConsumerChannelInboundHandler;
import com.yy.channelHandler.handler.RpcRequestEncoder;
import com.yy.channelHandler.handler.RpcResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 消费者通道初始化器类，用于在Netty的TCP客户端通道建立时配置其处理链。
 * 继承自ChannelInitializer，重写initChannel方法以初始化SocketChannel的处理链。
 */
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 当通道被初始化时，此方法被调用。它负责添加一系列处理器到通道的处理链中。
     *
     * @param socketChannel 需要被初始化的SocketChannel对象。
     * @throws Exception 如果初始化过程中发生错误。
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化通道的处理链
        socketChannel.pipeline()
                // 添加日志处理器，用于记录通道活动的日志，级别为DEBUG
                // netty自带的日志输出处理器
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                // 添加请求编码器，用于将客户端的RPC请求编码为适合网络传输的格式
                // 出站编码器
                .addLast(new RpcRequestEncoder())
                // 添加响应解码器，用于将服务端返回的RPC响应解码为客户端可处理的格式
                // 入站解码器
                .addLast(new RpcResponseDecoder())
                // 添加业务逻辑处理器，用于处理客户端接收到的RPC响应
                // 结果处理
                .addLast(new ConsumerChannelInboundHandler());
    }
}
