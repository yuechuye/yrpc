package com.yy.channelHandler.handler;

import com.yy.constants.MessageConstant;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author yuechu
 */
public class RpcRequestDecoder extends LengthFieldBasedFrameDecoder {
    public RpcRequestDecoder() {
        super(MessageConstant.MAX_FIELD_LENGTH,
                MessageConstant.LENGTH_FIELD_OFFSET,
                MessageConstant.LENGTH_FIELD_LENGTH,
                -(MessageConstant.LENGTH_FIELD_OFFSET + MessageConstant.LENGTH_FIELD_LENGTH),
                0);
    }
}
