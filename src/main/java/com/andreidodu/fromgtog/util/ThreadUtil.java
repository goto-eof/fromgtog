package com.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public static void executeOnSeparateThread(Runnable runnable) {
        final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

        SINGLE_THREAD_EXECUTOR.submit(() -> {
            log.debug("Repositories deletion started");
            runnable.run();
            System.out.println("Repositories deletion ended");
        });

        SINGLE_THREAD_EXECUTOR.shutdown();
    }


}
