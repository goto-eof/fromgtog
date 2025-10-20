package org.andreidodu.fromgtog.service.impl;

import org.andreidodu.fromgtog.dto.EngineContext;
import org.andreidodu.fromgtog.service.ScheduledService;
import org.andreidodu.fromgtog.util.CustomThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SingleThreadScheduledServiceImpl implements ScheduledService {

    private final static Logger log = LoggerFactory.getLogger(SingleThreadScheduledServiceImpl.class);

    private ScheduledFuture<?> future;

    @Getter
    @Setter
    private ScheduledExecutorService scheduledExecutorService;

    public SingleThreadScheduledServiceImpl(String threadName, EngineContext engineContext) {
        CustomThreadFactory customThreadFactory = new CustomThreadFactory(threadName);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, customThreadFactory);
        this.setScheduledExecutorService(scheduledExecutorService);
    }

    @Override
    public synchronized void run(Runnable runnable) {
        if (future != null) {
            return;
        }

        future = this.getScheduledExecutorService()
                .scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public synchronized void shutdown() {
        log.info("Shutting down ScheduledJobServiceImpl");
        if (future != null) {
            future.cancel(true);
        }
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
