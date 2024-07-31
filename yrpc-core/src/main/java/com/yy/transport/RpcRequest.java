package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * RPC请求类，用于封装远程过程调用的请求信息。
 */
public class RpcRequest {
    // 请求唯一标识，用于跟踪和识别请求。
    //请求id
    private long requestId;
    // 请求类型，用于标识请求的具体操作或服务。
    //请求类型
    private byte requestType;
    // 压缩类型，用于标识请求数据的压缩方式。
    //压缩类型
    private byte compressType;
    // 序列化类型，用于标识请求数据的序列化方式。
    //序列化方式
    private byte serializeType;
    // 请求负载，包含实际的请求数据。
    //消息体
    private RequestPayload requestPayload;
    // 时间戳，记录请求的发生时间。
    //时间戳
    private long timeStrap;
}
