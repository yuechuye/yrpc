//package com.yy.channelHandler.handler;
//
//import com.mhz.MessageConstant;
//import com.mhz.compress.CompressorFactory;
//import com.mhz.serializer.SerializeFactory;
//import com.mhz.serializer.Serializer;
//import com.mhz.transport.MrpcResponse;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class MrpcResponseDecoder extends LengthFieldBasedFrameDecoder {
//    public MrpcResponseDecoder() {
//        super(MessageConstant.MAX_FIELD_LENGTH,
//                MessageConstant.LENGTH_FIELD_OFFSET,
//                MessageConstant.LENGTH_FIELD_LENGTH,
//                -(MessageConstant.LENGTH_FIELD_OFFSET + MessageConstant.LENGTH_FIELD_LENGTH),
//                0);
//    }
//
//    @Override
//    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
//        Object decode = super.decode(ctx, in);
//        if (decode instanceof ByteBuf) {
//            ByteBuf byteBuf = (ByteBuf) decode;
//            return decodeFrame(byteBuf);
//        }
//        return null;
//    }
//
//
//    private Object decodeFrame(ByteBuf byteBuf) {
//        //解析魔术值
//        byte[] magic = new byte[MessageConstant.MAGIC.length];
//        byteBuf.readBytes(magic);
//        for (int i = 0; i < MessageConstant.MAGIC.length; i++) {
//            if (magic[i] != MessageConstant.MAGIC[i]) {
//                throw new RuntimeException("这个请求的魔术值不合法");
//            }
//        }
//        //解析版本号
//        byte version = byteBuf.readByte();
//        if (version > MessageConstant.VERSION) {
//            throw new RuntimeException("该版本不被支持");
//        }
//        //解析头部长度
//        short headLength = byteBuf.readShort();
//        //解析总长度
//        int fullLength = byteBuf.readInt();
//        //解析请求类型
//        byte code = byteBuf.readByte();
//        //解析序列化类型
//        byte serializeType = byteBuf.readByte();
//        //解析压缩类型
//        byte compressType = byteBuf.readByte();
//        //解析请求id
//        long requestId = byteBuf.readLong();
//        //解析时间戳
//        long timeStrap = byteBuf.readLong();
//
//        //封装响应
//        MrpcResponse mrpcResponse = MrpcResponse.builder()
//                .code(code)
//                .serializeType(serializeType)
//                .compressType(compressType)
//                .timeStrap(timeStrap)
//                .requestId(requestId).build();
//
//        //todo 心跳检测
//
//        int bodyLength = fullLength - headLength;
//
//        byte[] body = new byte[bodyLength];
//
//        byteBuf.readBytes(body);
//
//        if (body.length > 0){
//            //解压
//            body = CompressorFactory.getCompressorWrapper(compressType).getCompressor()
//                    .decompress(body);
//            //反序列化
//            Serializer serializer = SerializeFactory.getSerialize(mrpcResponse.getSerializeType()).getSerializer();
//            Object object = serializer.deserialize(body);
//            mrpcResponse.setBody(object);
//        }
//        log.debug("响应【{}】已经完成报文解码",mrpcResponse.getRequestId());
//        return mrpcResponse;
//    }
//
//
//}
