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
/**
 * RPC响应对象，用于封装RPC调用的返回结果。
 */
public class RpcResponse {

    // 请求的唯一标识符
    //请求id
    private long requestId;

    // 压缩类型，用于标识响应数据使用了哪种压缩算法
    private byte compressType;

    // 序列化类型，用于标识响应数据使用了哪种序列化方式
    private byte serializeType;

    // 响应状态码，用于标识响应的状态，如成功、失败等
    //响应码 2 成功  5 异常
    private byte code;

    // 响应的主体内容，即RPC调用返回的实际结果
    //响应体
    private Object body;

    // 时间戳，记录响应生成的时间
    //时间戳
    private long timeStrap;

}
