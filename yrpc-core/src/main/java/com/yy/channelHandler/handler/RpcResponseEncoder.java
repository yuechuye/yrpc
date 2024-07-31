package com.yy.channelHandler.handler;


import com.yy.common.MessageConstant;
import com.yy.compress.CompressorFactory;
import com.yy.serializer.SerializeFactory;
import com.yy.serializer.Serializer;
import com.yy.transport.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse RpcResponse, ByteBuf byteBuf) throws Exception {
        //4字节的魔术值
        byteBuf.writeBytes(MessageConstant.MAGIC);
        //1字节的版本号
        byteBuf.writeByte(MessageConstant.VERSION);
        //2字节的头部长度
        byteBuf.writeShort(MessageConstant.HEADER_LENGTH);
        //直接留四个字节给之后存总长度
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageConstant.LENGTH_FIELD_LENGTH);
        //3个类型
        byteBuf.writeByte(RpcResponse.getCode());
        byteBuf.writeByte(RpcResponse.getSerializeType());
        byteBuf.writeByte(RpcResponse.getCompressType());
        //8字节请求id
        byteBuf.writeLong(RpcResponse.getRequestId());
        //8字节时间戳
        byteBuf.writeLong(RpcResponse.getTimeStrap());

        //序列化请求body
        Serializer serializer = SerializeFactory.getSerialize(RpcResponse.getSerializeType()).getSerializer();
        byte[] bodyBytes = serializer.serialize(RpcResponse.getBody());
        //压缩
        bodyBytes = CompressorFactory.getCompressorWrapper(RpcResponse.getCompressType())
                .getCompressor().compress(bodyBytes);
        //检查是否是心跳检测
        if (bodyBytes != null){
            byteBuf.writeBytes(bodyBytes);
        }
        int bodyLength = bodyBytes == null ? 0 : bodyBytes.length;
        int writeIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(MessageConstant.LENGTH_FIELD_OFFSET);
        byteBuf.writeInt(MessageConstant.HEADER_LENGTH + bodyLength);
        //将写指针归位
        byteBuf.writerIndex(writeIndex);

        log.debug("响应【{}】已经完成报文编码",RpcResponse.getRequestId());
    }


}
