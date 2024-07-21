//package com.yy.channelHandler.handler;
//
//import com.mhz.MessageConstant;
//import com.mhz.MrpcBootStrap;
//import com.mhz.compress.CompressorFactory;
//import com.mhz.serializer.SerializeFactory;
//import com.mhz.serializer.SerializerWrapper;
//import com.mhz.transport.MrpcRequest;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class MrpcRequestEncoder extends MessageToByteEncoder<MrpcRequest> {
//    @Override
//    protected void encode(ChannelHandlerContext channelHandlerContext, MrpcRequest mrpcRequest, ByteBuf byteBuf) throws Exception {
//        //4字节的魔术值
//        byteBuf.writeBytes(MessageConstant.MAGIC);
//        //1字节的版本号
//        byteBuf.writeByte(MessageConstant.VERSION);
//        //2字节的头部长度
//        byteBuf.writeShort(MessageConstant.HEADER_LENGTH);
//        //直接留四个字节给之后存总长度
//        byteBuf.writerIndex(byteBuf.writerIndex() + MessageConstant.LENGTH_FIELD_LENGTH);
//        //3个类型
//        byteBuf.writeByte(mrpcRequest.getRequestType());
//        byteBuf.writeByte(mrpcRequest.getSerializeType());
//        byteBuf.writeByte(mrpcRequest.getCompressType());
//        //8字节请求id
//        byteBuf.writeLong(mrpcRequest.getRequestId());
//        //8字节的时间戳
//        byteBuf.writeLong(mrpcRequest.getTimeStrap());
//        //请求body
//        //序列化
//        SerializerWrapper serializerWrapper = SerializeFactory.getSerialize(MrpcBootStrap.getInstance()
//                .getConfiguration().getSerializeType());
//        byte[] bodyBytes = serializerWrapper.getSerializer().serialize(mrpcRequest.getRequestPayload());
//        //压缩
//        bodyBytes = CompressorFactory.getCompressorWrapper(mrpcRequest.getCompressType()).getCompressor()
//                .compress(bodyBytes);
//        //检查是否是心跳检测
//        if (bodyBytes != null){
//            byteBuf.writeBytes(bodyBytes);
//        }
//        int bodyLength = bodyBytes == null ? 0 : bodyBytes.length;
//        int writeIndex = byteBuf.writerIndex();
//        byteBuf.writerIndex(MessageConstant.LENGTH_FIELD_OFFSET);
//        byteBuf.writeInt(MessageConstant.HEADER_LENGTH + bodyLength);
//        //将写指针归位
//        byteBuf.writerIndex(writeIndex);
//
//        log.debug("请求【{}】已经完成报文编码",mrpcRequest.getRequestId());
//    }
//
//}
