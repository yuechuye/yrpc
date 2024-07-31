package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 请求载荷类，用于封装远程调用的请求信息。
 * 实现Serializable接口，以便于远程传输。
 */
public class RequestPayload implements Serializable {

    /**
     * 接口名称，表示远程调用的目标接口。
     */
    //接口名字
    private String interfaceName;

    /**
     * 方法名称，表示远程调用的具体方法。
     */
    //方法的名字
    private String methodName;

    /**
     * 参数类型数组，表示远程调用方法的参数类型。
     */
    //参数类型
    private Class<?>[] paramType;

    /**
     * 参数值数组，表示远程调用方法的参数值。
     */
    //参数值
    private Object[] paramValue;

    /**
     * 返回类型，表示远程调用方法的返回类型。
     */
    //返回值
    private Class<?> returnType;
}
