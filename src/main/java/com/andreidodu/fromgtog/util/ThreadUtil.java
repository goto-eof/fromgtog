package com.andreidodu.fromgtog.util;

import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.CustomExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            ExecutorService fixedThreadExecutorService = Executors.newFixedThreadPool(nThreads, new CustomThreadFactory(threadNamePrefix));
            return new CustomExecutorService(fixedThreadExecutorService);
        }

        ExecutorService singleThreadExecutorService = Executors.newSingleThreadExecutor(new CustomThreadFactory(threadNamePrefix));
        return new CustomExecutorService(singleThreadExecutorService);
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
        ExecutorService singleThreadExecutorService = Executors.newSingleThreadExecutor(new CustomThreadFactory(threadName));

        singleThreadExecutorService.execute(() -> {

            log.debug("Thread task started");
            runnable.run();
            log.debug("Thread task ended");

            singleThreadExecutorService.shutdown();

        });

    }

}
