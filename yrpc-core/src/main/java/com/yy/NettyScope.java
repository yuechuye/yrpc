//package com.yy;
//
//
//import com.yy.channelHandler.ConsumerChannelInitializer;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class NettyScope {
//    private static Bootstrap bootstrap = new Bootstrap();
//
//    private static EventLoopGroup group = new NioEventLoopGroup();
//
//    static {
//        bootstrap = bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .handler(new ConsumerChannelInitializer());
//    }
//
//    private NettyScope() {
//    }
//
//    public static Bootstrap getInstance(){
//        return bootstrap;
//    }
//
//}
