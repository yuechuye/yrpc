package com.yy.discovery;

import lombok.Data;

/**
 * 服务配置类，用于配置服务的相关信息，如接口、实现类和分组。
 * 通过该类可以方便地设置和获取服务的提供者接口、实现对象以及服务的分组信息。
 *
 * @param <T> 服务实现类的类型参数，用于泛型编程，增强代码的类型安全性和可读性。
 * @author yuechu
 */
@Data
public class ServiceConfig<T> {

    /**
     * 服务提供者接口类，用于指定服务的接口。
     * 该字段用于在运行时动态获取服务提供者的接口信息，以便进行服务的查找和调用。
     * -- GETTER --
     * 获取服务提供者接口类。
     */
    private Class<?> interfaceProvider;

    /**
     * 服务实现对象，用于指定服务的具体实现。
     * 该字段用于存储服务的实现对象，以便在客户端直接调用该对象的方法，实现服务的消费。
     * -- GETTER --
     * 获取服务实现对象。
     * <p>
     * -- SETTER --
     * 设置服务实现对象。
     */

    private Object ref;

    /**
     * 服务的分组，用于对服务进行分类和管理。
     * 通过分组，可以将相同类型或相关联的服务组织在一起，方便管理和调用。
     * -- GETTER --
     * 获取服务的分组。
     * <p>
     * -- SETTER --
     * 设置服务的分组。
     */

    private String group;

    /**
     * 设置服务提供者接口类。
     *
     * @param interfaceProvider 服务提供者的接口类。
     */
    public void setInterface(Class<?> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

}
