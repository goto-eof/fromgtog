package com.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.*;

public class ThreadUtil {

    private static ThreadUtil instance;

    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    public ThreadUtil() {
        if (instance != null) {
            throw new IllegalStateException("ThreadUtil already initialized");
        }
    }

    public static ThreadUtil getInstance() {
        if (instance == null) {
            instance = new ThreadUtil();
        }
        return instance;
    }

    public ExecutorService createExecutor(boolean isMultithread, boolean isVirtualThreadsEnabled) {
        int nThreads = calculateNumThreads(isMultithread, isVirtualThreadsEnabled);

        log.info("Creating a new threadpool using thread count {}", nThreads);

        if (isVirtualThreadsEnabled) {
            log.info("Virtual Threads Enabled");
            return new LimitedVirtualThreadsExecutorService(nThreads, CLONER_VIRTUAL_THREAD_NAME_PREFIX);
        }

        if (isMultithread) {
            log.info("Platform Threads Enabled");
        }

        CustomPlatformThreadFactory platformThreadFactory = new CustomPlatformThreadFactory(CLONER_PLATFORM_THREAD_NAME_PREFIX);
        return Executors.newFixedThreadPool(nThreads, platformThreadFactory);
    }

    private int calculateNumThreads(boolean multithreadingEnabled, boolean isVirtualThreadsEnabled) {
        int numProcessors = Runtime.getRuntime().availableProcessors();

        if (isVirtualThreadsEnabled) {
            return Integer.min(numProcessors * 2, MAX_VIRTUAL_THREADS);
        }

        if (!multithreadingEnabled) {
            return 1;
        }

        if (numProcessors - 2 > 1) {
            return numProcessors - 2;
        }

        return numProcessors;
    }

    public void executeOnSeparateThread(String threadName, Runnable runnable) {
        final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(new CustomPlatformThreadFactory(threadName));

        singleThreadExecutor.submit(() -> {
            log.debug("Thread task started");
            runnable.run();
            System.out.println("Thread task ended");
        });

        singleThreadExecutor.shutdown();
    }


    public void waitUntilShutDownCompleted(ExecutorService executorService) {
        executorService.shutdown();
        try {
            boolean outcome = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            log.debug("awaitTermination outcome: {}", outcome);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
