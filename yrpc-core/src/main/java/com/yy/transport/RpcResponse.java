package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC响应对象，用于封装远程过程调用的返回结果。
 * @author yuechu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse {
    /**
     * 请求的唯一标识符，用于匹配请求和响应。
     */
    private long requestId;

    /**
     * 压缩类型，指示响应数据的压缩方式。
     */
    private byte compressType;

    /**
     * 序列化类型，指示响应数据的序列化方式。
     */
    private byte serializeType;

    /**
     * 响应码，用于表示响应的状态，如成功、失败等。
     * <p>
     * 2 表示成功
     * 5 表示异常
     */
    private byte code;

    /**
     * 响应的主体内容，即远程过程调用的实际返回结果。
     */
    private Object body;

    /**
     * 时间戳，记录响应生成的时间。
     */
    private long timeStrap;
}
