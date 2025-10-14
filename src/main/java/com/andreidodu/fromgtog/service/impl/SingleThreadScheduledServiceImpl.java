package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.ScheduledService;
import com.andreidodu.fromgtog.util.CustomThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.JOB_THREAD_NAME_PREFIX;

public class SingleThreadScheduledServiceImpl implements ScheduledService {

    private final static Logger log = LoggerFactory.getLogger(SingleThreadScheduledServiceImpl.class);

    private ScheduledFuture<?> trayIconFlashingFuture;

    @Getter
    @Setter
    private ScheduledExecutorService scheduledExecutorService;

    private final EngineContext engineContext;


    public SingleThreadScheduledServiceImpl(EngineContext engineContext) {
        this.engineContext = engineContext;
        CustomThreadFactory customThreadFactory = new CustomThreadFactory(JOB_THREAD_NAME_PREFIX);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, customThreadFactory);
        this.setScheduledExecutorService(scheduledExecutorService);
        reset();
    }

    private synchronized void reset() {
        engineContext.callbackContainer().jobTicker().accept(true);
    }

    @Override
    public synchronized void run(Runnable runnable) {
        if (trayIconFlashingFuture != null) {
            return;
        }

        trayIconFlashingFuture = this.getScheduledExecutorService()
                .scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public synchronized void shutdown() {
        log.info("Shutting down ScheduledJobServiceImpl");
        if (trayIconFlashingFuture != null) {
            trayIconFlashingFuture.cancel(true);
        }
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            engineContext.callbackContainer().setEnabledUI().accept(true);
            engineContext.callbackContainer().jobTicker().accept(true);
            engineContext.callbackContainer().setShouldStop().accept(true);
            engineContext.callbackContainer().setWorking().accept(false);
        }
    }


}
