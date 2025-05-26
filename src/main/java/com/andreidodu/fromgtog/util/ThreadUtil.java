package com.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    private final static ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public static void executeOnSeparateThread(Runnable runnable) {
        SINGLE_THREAD_EXECUTOR.submit(() -> {
            log.debug("Repositories deletion started");
            runnable.run();
            System.out.println("Repositories deletion ended");
        });
    }

    public static void shutdown() {
        SINGLE_THREAD_EXECUTOR.shutdown();
        try {
            if (!SINGLE_THREAD_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                SINGLE_THREAD_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            SINGLE_THREAD_EXECUTOR.shutdownNow();
        }
        log.debug("Thread shutdown completed");
    }

}
