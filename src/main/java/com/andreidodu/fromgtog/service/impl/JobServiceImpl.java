package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.EngineContext;
import com.andreidodu.fromgtog.service.JobService;
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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.andreidodu.fromgtog.constants.ApplicationConstants.JOB_THREAD_NAME_PREFIX;

public class JobServiceImpl implements JobService {

    private final static Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private Future<?> future;
    @Getter
    @Setter
    private ScheduledExecutorService ticTacJobExecutorService;

    private final EngineContext engineContext;


    public JobServiceImpl(EngineContext engineContext) {
        this.engineContext = engineContext;
        CustomThreadFactory customThreadFactory = new CustomThreadFactory(JOB_THREAD_NAME_PREFIX);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(customThreadFactory);
        this.setTicTacJobExecutorService(scheduledExecutorService);
        reset();
    }

    private synchronized void reset() {
        engineContext.callbackContainer().jobTicker().accept(true);
    }

    @Override
    public synchronized void runTicTak(Runnable runnable) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        Cron cron = parser.parse(engineContext.settingsContext().chronExpression());
        if (future == null) {
            future = this.getTicTacJobExecutorService().scheduleAtFixedRate(() -> {
                ExecutionTime executionTime = ExecutionTime.forCron(cron);
                ZonedDateTime now = ZonedDateTime.now();
                boolean isCronMatchesNow = isNow(executionTime, now);
                log.info("checking if need to run the job: {}", isCronMatchesNow);

                if (engineContext.callbackContainer().isWorking().get()) {
                    engineContext.callbackContainer().jobTicker().accept(false);
                } else {

                    String nowString = formatZoneDateTime(Optional.of(now));
                    String nextRunString = calculateNextExecutionString(executionTime, now);
                    engineContext.callbackContainer().updateApplicationStatusMessage()
                            .accept(String.format("[%s] next run on %s", nowString, nextRunString));
                }


                if (!engineContext.callbackContainer().isWorking().get() && !engineContext.callbackContainer().isShouldStop().get() && isCronMatchesNow) {
                    runnable.run();
                } else if (engineContext.callbackContainer().isShouldStop().get()) {
                    shutdown();
                }
            }, 0, 1, TimeUnit.SECONDS);
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
        if (next.isEmpty()) {
            return "";
        }
        return formatZoneDateTime(next);
    }

    private static String formatZoneDateTime(Optional<ZonedDateTime> next) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return next.get().toLocalDateTime().format(formatter);
    }

    @Override
    public synchronized void shutdown() {
        log.info("Shutting down JobServiceImpl");
        engineContext.callbackContainer().jobTicker().accept(true);
        if (future != null) {
            future.cancel(true);
        }
        ticTacJobExecutorService.shutdownNow();
    }


}
