package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.util.CustomThreadFactory;
import com.andreidodu.fromgtog.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.TICKER_THREAD_NAME_PREFIX;

public class TimeCounterService {

    private Logger logger = LoggerFactory.getLogger(TimeCounterService.class);

    @Getter
    @Setter
    private ScheduledExecutorService timeExecutorService;
    private Long startTime = 0L;
    private static final AtomicBoolean timeEnabled = new AtomicBoolean(false);
    ScheduledFuture<?> future = null;

    public TimeCounterService(Consumer<String> updateTimeLabel) {
        this.setTimeExecutorService(Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory(TICKER_THREAD_NAME_PREFIX)));
        runTimeCounter(updateTimeLabel);
        reset(updateTimeLabel);
    }


    private void reset(Consumer<String> updateTimeLabel) {
        this.startTime = System.currentTimeMillis();
        timeEnabled.set(true);
        updateTimeLabel.accept(String.format("%s", "00:00:00"));
    }

    private void runTimeCounter(Consumer<String> updateTimeLabel) {
        ScheduledFuture<?> future = this.timeExecutorService.scheduleAtFixedRate(() -> {
            if (timeEnabled.get()) {
                updateTimeLabel.accept(String.format("%s", TimeUtil.formatMillis(System.currentTimeMillis() - startTime)));
            } else {
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopCounter() {
        timeEnabled.set(false);
        timeExecutorService.shutdown();
    }


}
