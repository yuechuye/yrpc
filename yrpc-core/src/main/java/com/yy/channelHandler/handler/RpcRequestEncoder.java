package com.yy.channelHandler.handler;


import com.yy.RpcBootStrap;
import com.yy.common.MessageConstant;
import com.yy.compress.CompressorFactory;
import com.yy.serializer.SerializeFactory;
import com.yy.serializer.SerializerWrapper;
import com.yy.transport.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        //4字节的魔术值
        byteBuf.writeBytes(MessageConstant.MAGIC);
        //1字节的版本号
        byteBuf.writeByte(MessageConstant.VERSION);
        //2字节的头部长度
        byteBuf.writeShort(MessageConstant.HEADER_LENGTH);
        //直接留四个字节给之后存总长度
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageConstant.LENGTH_FIELD_LENGTH);
        //3个类型
        byteBuf.writeByte(rpcRequest.getRequestType());
        byteBuf.writeByte(rpcRequest.getSerializeType());
        byteBuf.writeByte(rpcRequest.getCompressType());
        //8字节请求id
        byteBuf.writeLong(rpcRequest.getRequestId());
        //8字节的时间戳
        byteBuf.writeLong(rpcRequest.getTimeStrap());
        //请求body
        //序列化
        SerializerWrapper serializerWrapper = SerializeFactory.getSerialize(RpcBootStrap.getInstance()
                .getConfiguration().getSerializeType());
        byte[] bodyBytes = serializerWrapper.getSerializer().serialize(rpcRequest.getRequestPayload());
        //压缩
        bodyBytes = CompressorFactory.getCompressorWrapper(rpcRequest.getCompressType()).getCompressor()
                .compress(bodyBytes);
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

        log.debug("请求【{}】已经完成报文编码",rpcRequest.getRequestId());
    }

}
