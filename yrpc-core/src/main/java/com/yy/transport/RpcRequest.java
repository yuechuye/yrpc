package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC（Remote Procedure Call）请求对象，用于封装远程调用的请求信息。
 * 包含请求的唯一标识、类型、压缩方式、序列化方式等信息，以及请求的具体内容和时间戳。
 * @author yuechu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {
    /**
     * 请求的唯一标识，用于跟踪和识别请求。
     */
    //请求id
    private long requestId;
    /**
     * 请求的类型，用于标识请求的具体业务类型或操作。
     */
    //请求类型
    private byte requestType;
    /**
     * 压缩类型，用于标识请求数据的压缩方式，以便在传输时进行解压缩。
     */
    //压缩类型
    private byte compressType;
    /**
     * 序列化方式，用于标识请求数据的序列化方式，以便在传输时进行反序列化。
     */
    //序列化方式
    private byte serializeType;
    /**
     * 请求的具体内容，包含调用的方法名、参数等信息。
     */
    //消息体
    private RequestPayload requestPayload;
    /**
     * 时间戳，记录请求的生成时间，用于请求的时效性判断或日志记录。
     */
    //时间戳
    private long timeStrap;
}
