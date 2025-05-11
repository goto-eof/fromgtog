package com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.commands;

import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.Command;
import com.andreidodu.fromgtog.service.factory.to.engines.strategies.common.records.ThreadStopCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadStopCheckCommand implements Command<Boolean> {
    final static Logger log = LoggerFactory.getLogger(ThreadStopCheckCommand.class);

    private final ThreadStopCommandContext threadStopCommandContext;

    public ThreadStopCheckCommand(ThreadStopCommandContext threadStopCommandContext) {
        this.threadStopCommandContext = threadStopCommandContext;
    }

    @Override
    public Boolean execute() {
        if (threadStopCommandContext.isShouldStopSupplier().get()) {
            String repositoryName = threadStopCommandContext.repositoryName();
            log.debug("skipping because {} because user stop request", repositoryName);
            threadStopCommandContext.updateApplicationStatusMessageConsumer()
                    .accept("Skipping repository because user stop request: " + repositoryName);
            return true;
        }
        return false;
    }
}
