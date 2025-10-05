package com.andreidodu.fromgtog.util;

import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class LimitedVirtualThreadsExecutorService implements ExecutorService {
    private final ExecutorService executorService;
    private final Semaphore semaphore;
    private volatile boolean shutdown;
    private static final Logger log = LoggerFactory.getLogger(LimitedVirtualThreadsExecutorService.class);

    public LimitedVirtualThreadsExecutorService(final int maxConcurrency, String threadNamePrefix) {
        log.debug("Creating limited virtual threads executor service.");
        log.debug("maxConcurrency: {}", maxConcurrency);
        log.debug("threadNamePrefix: {}", threadNamePrefix);

        this.semaphore = new Semaphore(maxConcurrency);
        ThreadFactory virtualThreadFactory = new CustomVirtualThreadFactory(threadNamePrefix);
        this.executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory);
    }

    @Override
    public void execute(Runnable command) {
        if (shutdown) {
            throw new RejectedExecutionException("Executor is shutdown");
        }

        executorService.submit(() -> {
            try {
                semaphore.acquire();
                command.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release();
            }
        });
    }

    @Override
    public void shutdown() {
        shutdown = true;
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown = true;
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> future = new FutureTask<>(task, result);
        execute(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<?> future = new FutureTask<>(task, null);
        execute(future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("invokeAll not implemented");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException("invokeAll with timeout not implemented");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("invokeAny not implemented");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("invokeAny with timeout not implemented");
    }


}
