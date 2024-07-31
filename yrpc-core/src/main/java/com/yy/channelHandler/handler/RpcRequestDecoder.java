package com.yy.channelHandler.handler;


import com.yy.common.MessageConstant;
import com.yy.compress.CompressorFactory;
import com.yy.enumerate.RequestType;
import com.yy.serializer.SerializeFactory;
import com.yy.serializer.Serializer;
import com.yy.transport.RequestPayload;
import com.yy.transport.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcRequestDecoder extends LengthFieldBasedFrameDecoder {
    public RpcRequestDecoder() {
        super(MessageConstant.MAX_FIELD_LENGTH,
                MessageConstant.LENGTH_FIELD_OFFSET,
                MessageConstant.LENGTH_FIELD_LENGTH,
                -(MessageConstant.LENGTH_FIELD_OFFSET + MessageConstant.LENGTH_FIELD_LENGTH),
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) decode;
            return decodeFrame(byteBuf);
        }
        return null;
    }


    private Object decodeFrame(ByteBuf byteBuf) {
        //解析魔术值
        byte[] magic = new byte[MessageConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        for (int i = 0; i < MessageConstant.MAGIC.length; i++) {
            if (magic[i] != MessageConstant.MAGIC[i]) {
                throw new RuntimeException("这个请求的魔术值不合法");
            }
        }
        //解析版本号
        byte version = byteBuf.readByte();
        if (version > MessageConstant.VERSION) {
            throw new RuntimeException("该版本不被支持");
        }
        //解析头部长度
        short headLength = byteBuf.readShort();
        //解析总长度
        int fullLength = byteBuf.readInt();
        //解析请求类型
        byte requestType = byteBuf.readByte();
        //解析序列化类型
        byte serializeType = byteBuf.readByte();
        //解析压缩类型
        byte compressType = byteBuf.readByte();
        //解析请求id
        long requestId = byteBuf.readLong();
        //解析时间戳
        long timeStrap = byteBuf.readLong();

        //封装请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestType(requestType)
                .serializeType(serializeType)
                .compressType(compressType)
                .timeStrap(timeStrap)
                .requestId(requestId).build();

        if (requestType == RequestType.HEARTBEAT.getI()){
            return rpcRequest;
        }

        int bodyLength = fullLength - headLength;

        byte[] payload = new byte[bodyLength];

        byteBuf.readBytes(payload);

        if (payload.length > 0){
            //解压
            payload = CompressorFactory.getCompressorWrapper(compressType).getCompressor()
                    .decompress(payload);
            //反序列化
//            try (ByteArrayInputStream bis = new ByteArrayInputStream(payload);
//                 ObjectInputStream ois = new ObjectInputStream(bis);
//            ){
//                RequestPayload requestPayload = (RequestPayload) ois.readObject();
//                RpcRequest.setRequestPayload(requestPayload);
//            } catch (IOException | ClassNotFoundException e) {
//                log.error("获取请求负载时发生异常");
//            }
            Serializer serializer = SerializeFactory.getSerialize(rpcRequest.getSerializeType()).getSerializer();
            Object object = serializer.deserialize(payload);
            rpcRequest.setRequestPayload((RequestPayload) object);
        }

        log.debug("请求【{}】已经完成报文解码",rpcRequest.getRequestId());
        return rpcRequest;
    }


}
