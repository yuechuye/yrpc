package com.yy.serializer;

/**
 * 序列化器接口定义了对象序列化和反序列化的方法。
 * 序列化是将对象转换为字节序列的过程，以便于存储或传输。
 * 反序列化是将字节序列恢复为原始对象的过程。
 */
public interface Serializer {
    /**
     * 将给定对象序列化为字节数组。
     *
     * @param object 需要被序列化的对象，可以是任意类型的对象。
     * @return 返回一个字节数组，代表序列化后的对象。
     */
    byte[] serialize(Object object);

    /**
     * 将给定的字节数组反序列化为对象。
     *
     * @param bytes 代表序列化后对象的字节数组。
     * @return 返回反序列化后的对象。对象的类型应与序列化前的对象类型相同。
     */
    Object deserialize(byte[] bytes);
}
