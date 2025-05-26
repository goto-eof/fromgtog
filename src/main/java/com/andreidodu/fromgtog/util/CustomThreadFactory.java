package com.andreidodu.fromgtog.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    public static final String THREAD_NAME_PREFIX = "Mr.Cloner-";
    private final AtomicInteger threadCount = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(THREAD_NAME_PREFIX + threadCount.getAndIncrement());
        return t;
    }
}
