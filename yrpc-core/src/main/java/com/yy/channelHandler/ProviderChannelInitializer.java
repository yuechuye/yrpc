package com.yy.channelHandler;

import com.yy.channelHandler.handler.MethodCallHandler;
import com.yy.channelHandler.handler.RpcRequestDecoder;
import com.yy.channelHandler.handler.RpcResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务提供者通道初始化类。
 * 该类负责在Netty服务器接收到新连接时初始化通信通道的处理链。
 * 它添加了几个关键的处理程序，包括日志记录、请求解码、方法调用处理和响应编码。
 * @author yuechu
 */
@Slf4j
public class ProviderChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 当一个新的SocketChannel被创建时，该方法会被调用以初始化其pipeline。
     * 它负责添加必要的处理器到pipeline中，以处理进来的RPC请求并发送响应。
     *
     * @param socketChannel 新创建的SocketChannel实例。
     * @throws Exception 如果初始化过程中发生任何异常。
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化SocketChannel的处理链。
        socketChannel.pipeline()
                // 添加日志记录处理器，用于记录网络通信的详细信息。
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                // 添加RPC请求解码器，用于将进来的字节流解码为可处理的RPC请求对象。
                .addLast(new RpcRequestDecoder())
                // 添加方法调用处理器，实际处理RPC请求并生成相应的响应。
                .addLast(new MethodCallHandler())
                // 添加RPC响应编码器，用于将生成的RPC响应对象编码为字节流准备发送。
                .addLast(new RpcResponseEncoder());
    }
}
