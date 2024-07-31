package com.yy.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求id生成器
 */
/**
 * ID生成器类，用于生成基于时间戳、机房号、机器号和序列号的唯一ID。
 */
public class IdGenerator {

    /**
     * ID生成的起始时间戳，用于计算相对于此时间的毫秒数。
     */
    //起始时间戳
    public static final long START_STAMP = DateUtils.get("2024-1-1").getTime();

    /**
     * 机房号占用的位数。
     */
    public static final long ROOM_BIT = 5L;
    /**
     * 机器号占用的位数。
     */
    public static final long MACHINE_BIT = 5L;
    /**
     * 序列号占用的位数。
     */
    public static final long SEQUENCE_BIT = 12L;

    /**
     * 机房号的最大值。
     */
    public static final long ROOM_MAX = ~(-1L << ROOM_BIT);
    /**
     * 机器号的最大值。
     */
    public static final long MACHINE_MAX = ~(-1L << MACHINE_BIT);
    /**
     * 序列号的最大值。
     */
    public static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    /**
     * 时间戳在ID中左移的位数。
     */
    public static final long TIME_STAMP_LEFT = ROOM_BIT + MACHINE_BIT + SEQUENCE_BIT;

    /**
     * 机房号在ID中左移的位数。
     */
    public static final long ROOM_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    /**
     * 机器号在ID中左移的位数。
     */
    public static final long MACHINE_LEFT = SEQUENCE_BIT;

    /**
     * 机房号。
     */
    private long roomId;

    /**
     * 机器号。
     */
    private long machineId;

    /**
     * 序列号，用于在同一时间戳内生成唯一的ID。
     */
    private AtomicLong sequenceId = new AtomicLong(0);

    /**
     * 上一个时间戳，用于确保ID的唯一性。
     */
    private long lastTimeStamp;

    /**
     * 构造函数，初始化ID生成器。
     *
     * @param roomId 机房号
     * @param machineId 机器号
     * @throws IllegalArgumentException 如果机房号或机器号超过最大值
     */
    public IdGenerator(long roomId, long machineId) {
        if (roomId > ROOM_MAX || machineId > MACHINE_MAX){
            throw new IllegalArgumentException("机房号id或者机器号id不合法");
        }
        this.roomId = roomId;
        this.machineId = machineId;
    }

    /**
     * 生成一个唯一的ID。
     *
     * @return 生成的ID
     * @throws RuntimeException 如果出现时钟回拨
     */
    public long getId(){
        long currentTime = System.currentTimeMillis();
        long timeStamp = currentTime - START_STAMP;

        if (timeStamp < lastTimeStamp){
            throw new RuntimeException("出现时钟回拨问题。");
        }

        if (timeStamp == lastTimeStamp){
            sequenceId.incrementAndGet();
            if (sequenceId.longValue() >= SEQUENCE_MAX){
                timeStamp = getNextTimeStamp();
                sequenceId.set(0);
            }
        } else {
            sequenceId.set(0);
        }

        lastTimeStamp = timeStamp;

        long sequence = sequenceId.longValue();

        return timeStamp << TIME_STAMP_LEFT | roomId << ROOM_LEFT | machineId << MACHINE_LEFT
                | sequence;
    }

    /**
     * 获取下一个时间戳，用于处理序列号溢出的情况。
     *
     * @return 下一个时间戳
     */
    private long getNextTimeStamp() {
        long timeStamp = System.currentTimeMillis() - START_STAMP;
        while (timeStamp == lastTimeStamp){
            timeStamp = System.currentTimeMillis() - START_STAMP;
        }
        return timeStamp;
    }

    /**
     * 主函数，用于演示ID生成器的使用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator(1,2);
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> System.out.println(idGenerator.getId())).start();
        }
    }
}
