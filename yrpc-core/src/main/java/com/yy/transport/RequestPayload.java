package com.yy.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求载荷类，用于封装远程调用请求的相关信息。
 *
 * @author yuechu
 * @@Data Lombok注解，用于自动生成getter和setter方法
 * @AllArgsConstructor Lombok注解，用于生成全参构造方法
 * @NoArgsConstructor Lombok注解，用于生成无参构造方法
 * @Builder Lombok注解，用于生成Builder模式的构造方法
 * @Serializable 表明此类的实例可以被序列化，用于远程调用中的数据传输
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPayload implements Serializable {

    /**
     * 接口名称，表示远程调用的目标接口。
     */
    private String interfaceName;

    /**
     * 方法名称，表示远程调用的具体方法。
     */
    private String methodName;

    /**
     * 参数类型数组，表示远程调用方法的参数类型。
     */
    private Class<?>[] paramType;

    /**
     * 参数值数组，表示远程调用方法的参数值。
     */
    private Object[] paramValue;

    /**
     * 返回类型，表示远程调用方法的返回类型。
     */
    private Class<?> returnType;
}
