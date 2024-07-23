package com.yy.constants;

import java.nio.charset.StandardCharsets;

/**
 * MessageConstant类用于定义消息相关的常量。
 * 这些常量包括魔数、版本号、头部长度、最大字段长度等，用于规范和标识消息格式。
 * @author yuechu
 */
public class MessageConstant {

    /**
     * 魔数，用于标识消息的开始。
     */
    public static final byte[] MAGIC = "rpc".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息的版本号。
     * 使用byte类型表示版本号，当前版本为1。
     * 版本号用于兼容不同版本的协议或处理程序。
     */
    public static final byte VERSION = (byte) 1;

    /**
     * 消息头部的长度。
     * 定义头部长度为30字节，用于存放必要的消息头信息。
     */
    public static final short HEADER_LENGTH = 30;

    /**
     * 消息中单个字段的最大长度。
     * 设置最大字段长度为1MB，以限制消息体的大小，防止过大的消息导致内存溢出。
     */
    public static final int MAX_FIELD_LENGTH = 1024 * 1024;

    /**
     * 消息长度字段的偏移量。
     * 消息长度字段从第7个字节开始，用于指示消息体的长度。
     */
    public static final int LENGTH_FIELD_OFFSET = 7;

    /**
     * 消息长度字段的长度。
     * 消息长度字段占用4个字节，用于存储消息体的长度。
     */
    public static final int LENGTH_FIELD_LENGTH = 4;
}
