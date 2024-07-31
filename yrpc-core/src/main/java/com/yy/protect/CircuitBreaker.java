package com.yy.protect;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器类，用于实现电路熔断功能。
 * 当错误请求达到一定数量或错误率超过预设阈值时，熔断器将打开，阻止进一步的请求。
 * 当熔断器打开后，经过一段时间，它会进入半打开状态，允许一个请求通过来检测系统是否恢复。
 */
public class CircuitBreaker {
    // 熔断器状态，true表示熔断器打开，阻止请求
    //熔断器是否开启
    private volatile boolean isOpen = false;

    // 记录总请求次数
    //请求数
    private AtomicInteger requestCount = new AtomicInteger(0);

    // 记录错误请求次数
    //异常请求数
    private AtomicInteger errorRequest = new AtomicInteger(0);

    // 允许的最大错误请求次数
    private int maxErrorRequest;
    // 允许的最大错误率
    private float maxErrorRate;

    /**
     * 构造函数，初始化熔断器的最大错误请求次数和最大错误率。
     * @param maxErrorRequest 允许的最大错误请求次数
     * @param maxErrorRate 允许的最大错误率
     */
    public CircuitBreaker(int maxErrorRequest, float maxErrorRate) {
        this.maxErrorRequest = maxErrorRequest;
        this.maxErrorRate = maxErrorRate;
    }

    /**
     * 检查是否应该熔断请求。
     * 如果熔断器已经打开，或者当前错误请求次数超过阈值，或者错误率超过阈值，则返回true，表示应该熔断请求。
     * @return 如果应该熔断请求，则返回true；否则返回false。
     */
    /**
     * 检查是否应该开启熔断器。
     *
     * 熔断器是一种保护机制，当系统处于高错误率或超过预设的错误请求阈值时，会开启以阻止进一步的请求，从而保护系统免受持续的异常影响。
     *
     * @return 如果熔断器应该开启，则返回true；否则返回false。
     */
    public boolean isBreak(){
        // 检查熔断器是否已经开启，如果是，则无需进一步检查直接返回true。
        //如果熔断器开启着直接返回true
        if (isOpen){
            return true;
        }
        // 检查当前错误请求数量是否超过预设的最大错误请求阈值。
        // 触发的阈值
        if (errorRequest.get() > maxErrorRequest){
            isOpen = true;
            return true;
        }
        // 如果错误请求数量大于0且请求总数大于0，并且错误率超过预设的最大错误率，则开启熔断器。
        if (errorRequest.get() > 0 && requestCount.get() > 0
                && errorRequest.get() / (float)requestCount.get() > maxErrorRate){
            isOpen = true;
            return true;
        }
        // 如果以上条件都不满足，则表示熔断器不应该开启，返回false。
        return false;
    }


    /**
     * 记录一次请求。
     */
    public void recordRequest(){
        requestCount.incrementAndGet();
    }

    /**
     * 记录一次错误请求。
     */
    public void recordErrorRequest(){
        errorRequest.incrementAndGet();
    }

    /**
     * 重置熔断器状态，清除请求和错误请求计数。
     */
    /**
     * 重置熔断器
     */
    public void reset(){
        isOpen = false;
        requestCount.set(0);
        errorRequest.set(0);
    }

    /**
     * 程序入口，模拟请求并测试熔断器功能。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        CircuitBreaker circuitBreaker = new CircuitBreaker(3,1.1F);
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                circuitBreaker.recordRequest();
                int num = new Random().nextInt(100);
                if (num > 70){
                    circuitBreaker.recordErrorRequest();
                }
                boolean aBreak = circuitBreaker.isBreak();
                if (aBreak){
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            circuitBreaker.reset();
                        }
                    }, 5000);

                }
                String result = aBreak ? "熔断器阻断了请求" : "熔断器放行了请求";
                System.out.println(result);
            }
        }).start();
    }
}
