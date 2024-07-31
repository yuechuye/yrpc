package com.yy.serializer;


import com.yy.serializer.impl.HessianSerializer;
import com.yy.serializer.impl.JdkSerializer;
import com.yy.serializer.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工厂
 */
@Slf4j
/**
 * 序列化工厂类，用于根据序列化类型获取对应的序列化器。
 * 提供了根据类型名称和类型代码获取序列化器的方法。
 */
public class SerializeFactory {

    /**
     * 通过类型名称缓存序列化器，提高获取效率。
     */
    private static final Map<String, SerializerWrapper> SERIALIZER_CACHE = new ConcurrentHashMap<>(8);

    /**
     * 通过类型代码缓存序列化器，提高获取效率。
     */
    private static final Map<Byte, SerializerWrapper> SERIALIZER_CACHE_CODE = new ConcurrentHashMap<>(8);

    /**
     * 静态初始化块，加载并缓存常用的序列化器。
     * 初始化jdk、json和hessian三种序列化器。
     */
    static {
        SerializerWrapper jdk = new SerializerWrapper((byte) 1, "jdk", new JdkSerializer());
        SerializerWrapper json = new SerializerWrapper((byte) 2, "json", new JsonSerializer());
        SerializerWrapper hessian = new SerializerWrapper((byte) 3, "hessian", new HessianSerializer());
        SERIALIZER_CACHE.put("jdk", jdk);
        SERIALIZER_CACHE.put("json", json);
        SERIALIZER_CACHE.put("hessian", hessian);
        SERIALIZER_CACHE_CODE.put(jdk.getCode(), jdk);
        SERIALIZER_CACHE_CODE.put(json.getCode(), json);
        SERIALIZER_CACHE_CODE.put(hessian.getCode(), hessian);
    }

    /**
     * 根据序列化类型名称获取序列化器。
     * 如果指定的序列化器不存在，则返回默认的jdk序列化器。
     *
     * @param serializeType 序列化类型名称。
     * @return 对应的序列化器包装类。
     */
    public static SerializerWrapper getSerialize(String serializeType) {
        SerializerWrapper serializerWrapper = SERIALIZER_CACHE.get(serializeType);
        if (serializerWrapper == null) {
            log.debug("没有获取到【{}】序列化器", serializeType);
            return SERIALIZER_CACHE.get("jdk");
        }
        return SERIALIZER_CACHE.get(serializeType);
    }

    /**
     * 根据序列化类型代码获取序列化器。
     * 如果指定的序列化器不存在，则返回默认的jdk序列化器。
     *
     * @param serializeType 序列化类型代码。
     * @return 对应的序列化器包装类。
     */
    public static SerializerWrapper getSerialize(byte serializeType) {
        SerializerWrapper serializerWrapper = SERIALIZER_CACHE_CODE.get(serializeType);
        if (serializerWrapper == null) {
            log.debug("没有获取到【{}】序列化器", serializeType);
            return SERIALIZER_CACHE_CODE.get((byte) 1);
        }
        return SERIALIZER_CACHE_CODE.get(serializeType);
    }
}
