package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomExecutorService extends AbstractExecutorService {

    private final ExecutorService executorService;

    public CustomExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
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
    public void close() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
