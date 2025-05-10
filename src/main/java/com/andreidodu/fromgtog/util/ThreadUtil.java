package com.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public static void executeOnSeparateThread(Runnable runnable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            log.debug("Repositories deletion started");
            runnable.run();
            System.out.println("Repositories deletion ended");
        });

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        log.debug("Thread shutdown completed");
    }

}
