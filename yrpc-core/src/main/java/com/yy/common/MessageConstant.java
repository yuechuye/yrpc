package com.yy.common;

import java.nio.charset.StandardCharsets;

/**
 * MessageConstant 类用于定义消息通信中的常量。
 * 这些常量包括魔法数、版本号、头部长度、最大字段长度等，用于规范消息的格式和传输。
 */
public class MessageConstant {

    /**
     * 魔法数常量，用于标识消息的开始。
     * 选取"mrpc"作为魔法数，是为了在消息的开始部分明确标识出该消息是符合mrpc协议的。
     */
    public static final byte[] MAGIC = "yrpc".getBytes(StandardCharsets.UTF_8);

    /**
     * 版本号常量，用于标识协议的版本。
     * 版本号为1，表示当前协议的初始版本。
     */
    public static final byte VERSION = (byte) 1;

    /**
     * 头部长度常量，用于标识消息头部的长度。
     * 头部长度为30字节，包含了消息的基本信息，如消息长度、消息类型等。
     */
    public static final short HEADER_LENGTH = 30;

    /**
     * 最大字段长度常量，用于限制单个字段的最大长度。
     * 最大字段长度为1MB，防止单个字段过大导致内存问题。
     */
    public static final int MAX_FIELD_LENGTH = 1024 * 1024;

    /**
     * 长度字段偏移量常量，用于标识长度字段在消息中的位置。
     * 长度字段从第7个字节开始，用于存储消息体的长度。
     */
    public static final int LENGTH_FIELD_OFFSET = 7;

    /**
     * 长度字段长度常量，用于标识长度字段的长度。
     * 长度字段占用4字节，用于存储消息体的长度。
     */
    public static final int LENGTH_FIELD_LENGTH = 4;
}
