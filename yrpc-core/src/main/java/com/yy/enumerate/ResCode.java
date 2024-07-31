package com.yy.enumerate;

/**
 * 结果代码枚举，用于标准化响应码的定义和管理。
 * 每个枚举常量代表一种特定的响应状态，包括成功、心跳检测成功、限流、资源不存在和失败等情况。
 */
public enum ResCode {

    // 成功响应码，用于表示操作成功。
    SUCCESS((byte) 20,"成功"),
    // 心跳检测成功响应码，用于表示心跳检测操作成功。
    SUCCESS_HEART_BEAT((byte) 21,"心跳检测成功返回"),
    // 限流响应码，用于表示服务当前处于限流状态。
    RATE_LIMIT((byte)31,"服务被限流" ),
    // 资源不存在响应码，用于表示请求的资源无法找到。
    RESOURCE_NOT_FOUND((byte)44,"请求的资源不存在" ),
    // 通用失败响应码，用于表示操作失败或出现异常。
    FAIL((byte)50,"调用方法发生异常");

    // 响应码，用于唯一标识响应状态。
    private byte code;
    // 响应状态描述，用于对响应码进行文字解释。
    private String status;

    /**
     * 构造方法，用于初始化枚举常量。
     * @param code 响应码，类型为字节，确保响应码的唯一性。
     * @param status 响应状态描述，用于对响应码进行文字解释，便于理解和调试。
     */
    ResCode(byte code, String status) {
        this.code = code;
        this.status = status;
    }

    /**
     * 获取响应码。
     * @return 响应码，类型为字节。
     */
    public byte getCode() {
        return code;
    }

    /**
     * 获取响应状态描述。
     * @return 响应状态描述，用于对响应码进行文字解释。
     */
    public String getStatus() {
        return status;
    }
}

