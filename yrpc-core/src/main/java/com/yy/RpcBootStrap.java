package com.yy;

import com.yy.channelHandler.ProviderChannelInitializer;
import com.yy.config.Configuration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author yuechu
 */
public class RpcBootStrap {

    private static final RpcBootStrap RPC_BOOT_STRAP = new RpcBootStrap();

    private Configuration configuration;


    public RpcBootStrap() {
        this.configuration = new Configuration();
    }

    public static RpcBootStrap getInstance() {
        return RPC_BOOT_STRAP;
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * 启动netty服务
     */
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderChannelInitializer());

            ChannelFuture future = serverBootstrap.bind(configuration.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
