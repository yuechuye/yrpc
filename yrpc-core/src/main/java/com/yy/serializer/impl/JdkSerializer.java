package com.yy.serializer.impl;


import com.yy.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
/**
 * JDK序列化器实现类。
 * 该类实现了Serializer接口，使用JDK的序列化机制来实现对象的序列化和反序列化。
 */
public class JdkSerializer implements Serializer {
    /**
     * 对象序列化。
     * 将给定的对象转换为字节数组形式，以便于存储或传输。
     *
     * @param object 需要序列化的对象。
     * @return 序列化后的字节数组，如果对象为null则返回null。
     * @throws RuntimeException 如果序列化过程中发生IOException，则抛出运行时异常。
     */
    @Override
    public byte[] serialize(Object object) {
        if (object == null){
            return null;
        }

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            log.debug("对象【{}】已经完成序列化",object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化时发生异常：",e);
        }
    }

    /**
     * 对象反序列化。
     * 将给定的字节数组转换为相应的对象。
     *
     * @param bytes 已序列化的对象的字节数组。
     * @return 反序列化后的对象，如果字节数组为null则返回null。
     * @throws RuntimeException 如果反序列化过程中发生IOException或ClassNotFoundException，则抛出运行时异常。
     */
    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object object = ois.readObject();
            log.debug("对象【{}】已经完成反序列化", object);
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("反序列化时发生异常：", e);
        }
    }
}
