package com.geekbang.demo.complex.outbound;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Q
 */
public class HttpThreadFactory implements ThreadFactory {


    /**
     * 线程group
     */
    private final ThreadGroup group = Thread.currentThread().getThreadGroup();

    /**
     * 统计线程数，用AtomicInteger原子性操作
     */
    private final AtomicInteger threadCount = new AtomicInteger(1);

    /**
     * 线程名前缀
     */
    private final String namePrefix;

    /**
     * 是否为守护线程
     */
    private final boolean daemon;

    public HttpThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, namePrefix + "-thread-"+ threadCount.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }
}
