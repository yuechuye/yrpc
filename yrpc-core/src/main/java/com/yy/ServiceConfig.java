package com.yy;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务配置类，用于配置服务的相关信息，如接口类、实现类和分组。
 * 通过该类可以动态地设置和获取服务的配置，为服务的灵活管理和调用提供了便利。
 * <p>
 * 该类使用泛型T，允许配置任何类型的接口和实现。
 * @author yuechu
 */
@Getter
public class ServiceConfig<T> {

    /**
     * -- GETTER --
     *  获取服务接口的Class对象。
     *
     */
    // 保存服务接口的Class对象，用于后续的服务定位和调用。
    private Class<?> interfaceProvider;

    /**
     * -- GETTER --
     *  获取服务实现的实例对象。
     * <p>
     *
     * -- SETTER --
     *  设置服务实现的实例对象。
     *
     */
    // 保存服务实现的实例对象，用于后续的服务调用。
    @Setter
    private Object ref;

    /**
     * -- GETTER --
     *  获取服务的分组信息。
     * <p>
     * -- SETTER --
     *  设置服务的分组信息。
     *

     */
    // 保存服务的分组信息，用于区分不同的服务分组，便于服务的管理和调用。
    @Setter
    private String group;

    /**
     * 设置服务接口的Class对象。
     *
     * @param interfaceProvider 服务接口的Class对象，用于描述服务的接口类型。
     */
    public void setInterface(Class<?> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

}
