package com.yy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求ID生成器，负责生成全局唯一的ID。
 * 使用雪花算法为基础，结合时间戳、机器ID和序列号生成ID。
 */
public class IdGenerator {

    // 定义起始时间戳，用于计算相对于该时间的毫秒数
    //起始时间戳
    public static final long START_STAMP = DateUtils.get("2024-1-1").getTime();

    // 定义机房ID、机器ID和序列号的位数
    public static final long ROOM_BIT = 5L;
    public static final long MACHINE_BIT = 5L;
    public static final long SEQUENCE_BIT = 12L;

    // 根据位数计算各部分的最大值
    public static final long ROOM_MAX = ~(-1L << ROOM_BIT);
    public static final long MACHINE_MAX = ~(-1L << MACHINE_BIT);
    public static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    // 计算时间戳左移的位数
    public static final long TIME_STAMP_LEFT = ROOM_BIT + MACHINE_BIT + SEQUENCE_BIT;

    // 计算机房ID左移的位数
    public static final long ROOM_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    // 计算机器ID左移的位数
    public static final long MACHINE_LEFT = SEQUENCE_BIT;

    // 机房ID
    private long roomId;

    // 机器ID
    private long machineId;

    // 序列号，使用AtomicLong保证线程安全
    private AtomicLong sequenceId = new AtomicLong(0);

    // 上一次生成ID的时间戳
    private long lastTimeStamp;

    /**
     * 构造函数，初始化IdGenerator。
     *
     * @param roomId 机房ID
     * @param machineId 机器ID
     * @throws IllegalArgumentException 如果机房ID或机器ID超过最大值，抛出该异常
     */
    public IdGenerator(long roomId, long machineId) {
        if (roomId > ROOM_MAX || machineId > MACHINE_MAX){
            throw new IllegalArgumentException("机房号id或者机器号id不合法");
        }
        this.roomId = roomId;
        this.machineId = machineId;
    }

    /**
     * 生成唯一的ID。
     *
     * @return 生成的ID
     * @throws RuntimeException 如果出现时钟回拨，抛出该异常
     */
    public long getId(){
        long currentTime = System.currentTimeMillis();
        long timeStamp = currentTime - START_STAMP;

        // 检查时钟回拨
        if (timeStamp < lastTimeStamp){
            throw new RuntimeException("出现时钟回拨问题。");
        }

        // 如果时间戳相同，递增序列号
        if (timeStamp == lastTimeStamp){
            sequenceId.incrementAndGet();
            if (sequenceId.longValue() >= SEQUENCE_MAX){
                timeStamp = getNextTimeStamp();
                sequenceId.set(0);
            }
        } else {
            sequenceId.set(0);
        }

        // 更新上一次生成ID的时间戳
        lastTimeStamp = timeStamp;

        // 组合各部分生成最终的ID
        long sequence = sequenceId.longValue();
        return timeStamp << TIME_STAMP_LEFT | roomId << ROOM_LEFT | machineId << MACHINE_LEFT
                | sequence;
    }

    /**
     * 获取下一个时间戳，用于处理时间戳回拨的情况。
     *
     * @return 下一个时间戳
     */
    private long getNextTimeStamp() {
        long timeStamp = System.currentTimeMillis() - START_STAMP;
        // 等待直到时间戳不再是上一个时间戳
        while (timeStamp == lastTimeStamp){
            timeStamp = System.currentTimeMillis() - START_STAMP;
        }
        return timeStamp;
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator(1,2);
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> System.out.println(idGenerator.getId())).start();
        }
    }
}
