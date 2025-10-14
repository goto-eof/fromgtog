package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.ScheduledService;
import com.andreidodu.fromgtog.util.CustomThreadFactory;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.JOB_THREAD_NAME_PREFIX;

public class ScheduledJobServiceImpl implements ScheduledService {

    private final static Logger log = LoggerFactory.getLogger(ScheduledJobServiceImpl.class);

    private ScheduledFuture<?> jobScheduledFuture;

    @Getter
    @Setter
    private ScheduledExecutorService scheduledExecutorService;

    private final EngineContext engineContext;


    public ScheduledJobServiceImpl(EngineContext engineContext) {
        this.engineContext = engineContext;
        CustomThreadFactory customThreadFactory = new CustomThreadFactory(JOB_THREAD_NAME_PREFIX);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, customThreadFactory);
        this.setScheduledExecutorService(scheduledExecutorService);
    }


    @Override
    public synchronized void run(Runnable runnable) {

        if (jobScheduledFuture != null) {
            return;
        }

        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        Cron cron = parser.parse(engineContext.settingsContext().chronExpression());

        jobScheduledFuture = this.getScheduledExecutorService()
                .scheduleAtFixedRate(buildRunnableRecurringTask(runnable, cron), 0, 1, TimeUnit.SECONDS);

    }

    private Runnable buildRunnableRecurringTask(Runnable runnable, Cron cron) {
        return () -> {
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            ZonedDateTime now = ZonedDateTime.now();
            boolean isCronMatchesNow = isNow(executionTime, now);
            log.debug("checking if need to run the job: {}", isCronMatchesNow);

            showNextJobRunInfoIfNecessary(now, executionTime);

            runJobIfNecessary(runnable, isCronMatchesNow);

            stopJobIfNecessary();
        };
    }

    private void stopJobIfNecessary() {
        if (isNecessaryToStopJob()) {
            engineContext.callbackContainer().jobTicker().accept(true);
            shutdown();
        }
    }

    private void runJobIfNecessary(Runnable runnable, boolean isCronMatchesNow) {
        if (isNecessaryToRunJob(isCronMatchesNow)) {
            runnable.run();
        }
    }

    private Boolean isNecessaryToStopJob() {
        return engineContext.callbackContainer().isShouldStop().get();
    }

    private boolean isNecessaryToRunJob(boolean isCronMatchesNow) {
        return !engineContext.callbackContainer().isWorking().get() && !isNecessaryToStopJob() && isCronMatchesNow;
    }

    private void showNextJobRunInfoIfNecessary(ZonedDateTime now, ExecutionTime executionTime) {
        if (!engineContext.callbackContainer().isWorking().get()) {
            String nowString = formatZoneDateTime(now);
            String nextRunString = calculateNextExecutionString(executionTime, now);
            engineContext.callbackContainer().updateApplicationStatusMessage()
                    .accept(String.format("[%s] next run on %s", nowString, nextRunString));
        }
    }

    private static boolean isNow(ExecutionTime executionTime, ZonedDateTime now) {
        Optional<ZonedDateTime> next = executionTime.nextExecution(now);
        if (next.isEmpty()) {
            return false;
        }
        long secondsUntilNext = now.until(next.get(), ChronoUnit.SECONDS);
        return secondsUntilNext == 0;
    }

    private static String calculateNextExecutionString(ExecutionTime executionTime, ZonedDateTime now) {
        Optional<ZonedDateTime> next = executionTime.nextExecution(now);
        return next.map(ScheduledJobServiceImpl::formatZoneDateTime)
                .orElse("");
    }

    private static String formatZoneDateTime(ZonedDateTime next) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(next)
                .orElseThrow(() -> new IllegalArgumentException("nextZoneDateTime cannot be null"))
                .toLocalDateTime()
                .format(formatter);
    }

    @Override
    public synchronized void shutdown() {
        log.info("Shutting down ScheduledJobServiceImpl");
        if (jobScheduledFuture != null) {
            jobScheduledFuture.cancel(true);
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
