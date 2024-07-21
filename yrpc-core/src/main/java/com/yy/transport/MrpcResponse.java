package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MrpcResponse {

    //请求id
    private long requestId;

    private byte compressType;

    private byte serializeType;

    //响应码 2 成功  5 异常
    private byte code;

    //响应体
    private Object body;

    //时间戳
    private long timeStrap;

}
