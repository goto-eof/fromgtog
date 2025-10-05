package com.andreidodu.fromgtog.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomVirtualThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private final AtomicInteger threadCount = new AtomicInteger(1);

    public CustomVirtualThreadFactory(final String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        return Thread.ofVirtual()
                .name(threadNamePrefix, threadCount.getAndIncrement())
                .unstarted(r);
    }
}
