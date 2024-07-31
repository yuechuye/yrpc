package com.yy.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.yy.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
/**
 * Hessian序列化器类，实现了Serializer接口，用于对象的序列化和反序列化。
 */
public class HessianSerializer implements Serializer {
    /**
     * 对象序列化方法，将给定的对象转换为字节数组。
     *
     * @param object 需要序列化的对象。
     * @return 返回序列化后的字节数组，如果对象为null则返回null。
     * @throws RuntimeException 如果序列化过程中发生IOException，则抛出运行时异常。
     */
    @Override
    public byte[] serialize(Object object) {
        if (object == null){
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            Hessian2Output hso = new Hessian2Output(bos);
            hso.writeObject(object);
            hso.flush();
            log.debug("对象【{}】Hessian序列化成功",object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Hessian序列化时出现异常：",e);
        }
    }

    /**
     * 对象反序列化方法，将给定的字节数组转换为对象。
     *
     * @param bytes 需要反序列化的字节数组。
     * @return 返回反序列化后的对象。
     * @throws RuntimeException 如果反序列化过程中发生IOException，则抛出运行时异常。
     */
    @Override
    public Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)){
            Hessian2Input hi = new Hessian2Input(bis);
            Object object = hi.readObject();
            log.debug("对象【{}】Hessian反序列化成功",object);
            return object;
        } catch (IOException e) {
            throw new RuntimeException("Hessian反序列化时出现异常",e);
        }
    }
}
