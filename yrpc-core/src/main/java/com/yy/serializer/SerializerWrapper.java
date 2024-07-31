package com.yy.serializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 序列化器包装类。
 * 该类提供了一个封装来管理特定序列化器的实例，允许根据不同的序列化需求动态切换。
 */
public class SerializerWrapper {

    /**
     * 序列化器代码标识。
     * 用于唯一标识一个序列化器，便于快速检索和选择合适的序列化器。
     */
    private byte code;

    /**
     * 序列化类型名称。
     * 描述了当前序列化器所使用的序列化方式，例如"JSON"、"XML"等，便于用户理解和选择。
     */
    private String serializeType;

    /**
     * 序列化器实例。
     * 存储了当前活跃的序列化器实例，该实例负责实际的序列化和反序列化操作。
     */
    private Serializer serializer;
}
