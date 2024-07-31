package com.yy.compress;


import com.yy.compress.impl.GzipCompressor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
/**
 * 压缩器工厂类，用于根据不同的压缩算法类型创建和获取压缩器实例。
 * 通过静态初始化块预先注册了gzip压缩器。
 */
public class CompressorFactory {
    /**
     * 以压缩算法名称为键，存储压缩器包装类的映射。
     * 使用ConcurrentHashMap以保证线程安全。
     */
    private static final Map<String,CompressWrapper> COMPRESSOR_CACHE = new ConcurrentHashMap<>(8);

    /**
     * 以压缩算法类型代码为键，存储压缩器包装类的映射。
     * 使用ConcurrentHashMap以保证线程安全。
     */
    private static final Map<Byte,CompressWrapper> COMPRESSOR_CACHE_CODE = new ConcurrentHashMap<>(8);

    /**
     * 静态初始化块，注册gzip压缩器。
     * 将gzip压缩器实例添加到压缩器缓存中，分别使用名称和类型代码作为键。
     */
    static {
        CompressWrapper gzip = new CompressWrapper((byte) 1, "gzip", new GzipCompressor());
        COMPRESSOR_CACHE.put("gzip", gzip);
        COMPRESSOR_CACHE_CODE.put((byte) 1, gzip);
    }

    /**
     * 根据压缩算法类型名称获取压缩器包装类。
     * 如果指定的压缩算法类型不存在，则返回默认的gzip压缩器。
     *
     * @param compressorType 压缩算法类型名称。
     * @return 压缩器包装类实例。
     */
    public static CompressWrapper getCompressorWrapper(String compressorType){
        if (!COMPRESSOR_CACHE.containsKey(compressorType)){
            log.debug("没有获取到设置的压缩器");
            return COMPRESSOR_CACHE.get("gzip");
        }
        return COMPRESSOR_CACHE.get(compressorType);
    }

    /**
     * 根据压缩算法类型代码获取压缩器包装类。
     * 如果指定的压缩算法类型代码不存在，则返回默认的gzip压缩器。
     *
     * @param compressorType 压缩算法类型代码。
     * @return 压缩器包装类实例。
     */
    public static CompressWrapper getCompressorWrapper(byte compressorType){
        if (!COMPRESSOR_CACHE_CODE.containsKey(compressorType)){
            log.debug("没有获取到设置的压缩器");
            return COMPRESSOR_CACHE_CODE.get((byte) 1);
        }
        return COMPRESSOR_CACHE_CODE.get(compressorType);
    }
}
