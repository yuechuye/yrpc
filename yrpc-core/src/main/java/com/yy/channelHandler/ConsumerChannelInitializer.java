//package com.yy.channelHandler;
//
//
//import com.yy.channelHandler.handler.ConsumerChannelInboundHandler;
//import com.yy.channelHandler.handler.MrpcRequestEncoder;
//import com.yy.channelHandler.handler.MrpcResponseDecoder;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
//public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
//    @Override
//    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        socketChannel.pipeline()
//                //netty自带的日志输出处理器
//                .addLast(new LoggingHandler(LogLevel.DEBUG))
//                //出站编码器
//                .addLast(new MrpcRequestEncoder())
//                //入站解码器
//                .addLast(new MrpcResponseDecoder())
//                //结果处理
//                .addLast(new ConsumerChannelInboundHandler());
//
//    }
//}
