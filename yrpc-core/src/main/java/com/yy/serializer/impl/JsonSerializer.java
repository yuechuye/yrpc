package com.yy.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.yy.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * JsonSerializer类实现了Serializer接口，用于对象的序列化和反序列化。
 * 它使用JSON格式进行数据转换，提供了将Java对象转换为JSON字节序列和将JSON字节序列转换回Java对象的方法。
 */
public class JsonSerializer implements Serializer {
    /**
     * 将给定的对象序列化为JSON字节序列。
     *
     * @param object 需要序列化的对象，可以为任意类型的对象。
     * @return 如果对象为null，则返回null；否则返回对象序列化后的JSON字节序列。
     */
    @Override
    public byte[] serialize(Object object) {
        if (object == null){
            return null;
        }

        return JSON.toJSONBytes(object);
    }

    /**
     * 将给定的JSON字节序列反序列化为Java对象。
     *
     * @param bytes JSON字节序列，代表一个序列化后的对象。
     * @return 如果字节序列为null，则返回null；否则返回反序列化后的Java对象。
     */
    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        return JSON.parse(bytes);
    }
}

