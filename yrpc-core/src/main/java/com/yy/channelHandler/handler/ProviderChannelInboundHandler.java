package com.yy.channelHandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ProviderChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        log.info("byteBuf -> {}", msg.toString(StandardCharsets.UTF_8));
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("mrpc--hello".getBytes(StandardCharsets.UTF_8)));
    }
}
