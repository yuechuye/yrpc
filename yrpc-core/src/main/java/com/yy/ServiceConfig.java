package com.yy;

/**
 * 服务配置类，用于配置服务的相关信息，如接口、实现类和分组。
 * 通过该类可以动态地设置和获取服务的配置，为服务的灵活管理和调用提供了便利。
 *
 * @param <T> 服务的实现类类型，泛型设计使得该类可以应用于不同类型的服務配置。
 */
public class ServiceConfig<T> {
    // 服务接口类，用于指定服务的接口，以便于服务的消费者根据接口进行调用。
    private Class<?> interfaceProvider;
    // 服务的实现类实例，用于指定服务的具体实现，以便于服务的提供者可以提供具体的服务实现。
    private Object ref;
    // 服务的分组，用于对服务进行分类和管理，可以提供更加灵活的服务发现和调用。
    private String group;

    /**
     * 设置服务接口类。
     *
     * @param interfaceProvider 服务接口类，用于指定服务的接口。
     */
    public void setInterface(Class<?> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    /**
     * 设置服务的实现类实例。
     *
     * @param ref 服务的实现类实例，用于指定服务的具体实现。
     */
    public void setRef(Object ref) {
        this.ref = ref;
    }

    /**
     * 获取服务接口类。
     *
     * @return 服务接口类，用于获取服务的接口。
     */
    public Class<?> getInterfaceProvider() {
        return interfaceProvider;
    }

    /**
     * 获取服务的实现类实例。
     *
     * @return 服务的实现类实例，用于获取服务的具体实现。
     */
    public Object getRef() {
        return ref;
    }

    /**
     * 设置服务的分组。
     *
     * @param group 服务的分组，用于对服务进行分类和管理。
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 获取服务的分组。
     *
     * @return 服务的分组，用于获取服务的分类信息。
     */
    public String getGroup() {
        return group;
    }
}
