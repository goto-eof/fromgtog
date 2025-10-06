package com.andreidodu.fromgtog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.MAX_NUM_THREADS;

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

    public ExecutorService createExecutor(String threadNamePrefix, boolean isMultithread) {

        int nThreads = calculateNumThreads(isMultithread);

        if (nThreads > 1) {
            log.info("Creating a new threadpool using thread count {}", nThreads);
            return Executors.newFixedThreadPool(nThreads, new CustomThreadFactory(threadNamePrefix));
        }

        return Executors.newSingleThreadExecutor(new CustomThreadFactory(threadNamePrefix));
    }

    private int calculateNumThreads(boolean multithreadingEnabled) {
        if (!multithreadingEnabled) {
            return 1;
        }
        int numProcessors = Runtime.getRuntime().availableProcessors();

        if (numProcessors > 1) {
            numProcessors -= 1;
        }

        return Math.min(numProcessors, MAX_NUM_THREADS);
    }

    public void executeOnSeparateThread(String threadName, Runnable runnable) {
        final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(new CustomThreadFactory(threadName));

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
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
