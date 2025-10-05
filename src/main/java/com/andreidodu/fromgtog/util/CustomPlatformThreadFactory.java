package com.andreidodu.fromgtog.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomPlatformThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private final AtomicInteger threadCount = new AtomicInteger(1);

    public CustomPlatformThreadFactory(final String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(threadNamePrefix + threadCount.getAndIncrement());
        return t;
    }
}
