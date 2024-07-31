package com.yy;

import com.yy.channelHandler.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyScope类用于管理Netty的Bootstrap实例，确保其在应用程序中被正确初始化和重用。
 * 该类采用单例模式，隐藏了Bootstrap实例的创建和配置细节，客户端通过调用getInstance方法获取Bootstrap实例。
 */
@Slf4j
public class NettyScope {
    // Bootstrap实例，用于配置和初始化Netty客户端
    private static Bootstrap bootstrap = new Bootstrap();

    // EventLoopGroup实例，用于处理I/O事件
    private static EventLoopGroup group = new NioEventLoopGroup();

    // 静态块用于初始化Bootstrap实例，配置其使用NioEventLoopGroup和ConsumerChannelInitializer
    static {
        bootstrap = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
    }

    // 私有构造方法，防止外部实例化NettyScope类
    private NettyScope() {
    }

    /**
     * 获取NettyBootstrap的单例实例。
     *
     * @return Bootstrap的单例实例，用于创建和配置Netty客户端通道。
     */
    public static Bootstrap getInstance(){
        return bootstrap;
    }

}

